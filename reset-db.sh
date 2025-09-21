#!/bin/bash

# Reset Database Script for Transforma-Faces
# This script resets the local SQLite database by removing the database file
# and optionally rebuilding the project to recreate it with fresh data.

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DB_FILE="$PROJECT_ROOT/transforma_tasks.db"
TARGET_DIR="$PROJECT_ROOT/target"

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to stop any running Tomcat instances
stop_tomcat() {
    print_status "Checking for running Tomcat instances..."
    
    # Check for Tomcat processes
    TOMCAT_PIDS=$(pgrep -f "tomcat" 2>/dev/null || true)
    
    if [ -n "$TOMCAT_PIDS" ]; then
        print_warning "Found running Tomcat processes: $TOMCAT_PIDS"
        print_status "Stopping Tomcat processes..."
        
        # Try graceful shutdown first
        pkill -f "tomcat" 2>/dev/null || true
        sleep 2
        
        # Force kill if still running
        TOMCAT_PIDS=$(pgrep -f "tomcat" 2>/dev/null || true)
        if [ -n "$TOMCAT_PIDS" ]; then
            print_warning "Force killing remaining Tomcat processes..."
            pkill -9 -f "tomcat" 2>/dev/null || true
        fi
        
        print_success "Tomcat processes stopped"
    else
        print_status "No running Tomcat processes found"
    fi
}

# Function to backup existing database
backup_database() {
    if [ -f "$DB_FILE" ]; then
        BACKUP_FILE="${DB_FILE}.backup.$(date +%Y%m%d_%H%M%S)"
        print_status "Creating backup of existing database..."
        cp "$DB_FILE" "$BACKUP_FILE"
        print_success "Database backed up to: $BACKUP_FILE"
    fi
}

# Function to remove database file
remove_database() {
    if [ -f "$DB_FILE" ]; then
        print_status "Removing existing database file: $DB_FILE"
        rm -f "$DB_FILE"
        print_success "Database file removed"
    else
        print_status "No existing database file found"
    fi
}

# Function to clean Maven target directory
clean_target() {
    if [ -d "$TARGET_DIR" ]; then
        print_status "Cleaning Maven target directory..."
        rm -rf "$TARGET_DIR"
        print_success "Target directory cleaned"
    fi
}

# Function to rebuild project
rebuild_project() {
    print_status "Rebuilding project with Maven..."
    
    if ! command_exists mvn; then
        print_error "Maven not found. Please install Maven to rebuild the project."
        return 1
    fi
    
    # Compile and package the project
    mvn clean compile package -DskipTests
    
    if [ $? -eq 0 ]; then
        print_success "Project rebuilt successfully"
    else
        print_error "Failed to rebuild project"
        return 1
    fi
}

# Function to verify database reset
verify_reset() {
    print_status "Verifying database reset..."
    
    if [ -f "$DB_FILE" ]; then
        print_success "Database file recreated: $DB_FILE"
        
        # Check if we can query the database
        if command_exists sqlite3; then
            print_status "Checking database schema..."
            sqlite3 "$DB_FILE" ".schema" > /dev/null 2>&1
            if [ $? -eq 0 ]; then
                print_success "Database schema verified"
                
                # Count tasks
                TASK_COUNT=$(sqlite3 "$DB_FILE" "SELECT COUNT(*) FROM tasks;" 2>/dev/null || echo "0")
                print_status "Database contains $TASK_COUNT tasks"
            else
                print_warning "Could not verify database schema"
            fi
        else
            print_warning "sqlite3 command not found. Cannot verify database contents."
        fi
    else
        print_error "Database file was not recreated"
        return 1
    fi
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help          Show this help message"
    echo "  -b, --backup        Create backup before reset (default: true)"
    echo "  -n, --no-backup     Skip backup creation"
    echo "  -r, --rebuild       Rebuild project after reset (default: true)"
    echo "  -s, --skip-rebuild  Skip project rebuild"
    echo "  -c, --clean-only    Only clean database, don't rebuild"
    echo "  -v, --verbose       Enable verbose output"
    echo ""
    echo "Examples:"
    echo "  $0                  # Reset with backup and rebuild"
    echo "  $0 --no-backup      # Reset without backup"
    echo "  $0 --clean-only     # Only remove database file"
    echo "  $0 --skip-rebuild   # Reset but don't rebuild project"
}

# Main function
main() {
    local CREATE_BACKUP=true
    local REBUILD_PROJECT=true
    local CLEAN_ONLY=false
    local VERBOSE=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            -b|--backup)
                CREATE_BACKUP=true
                shift
                ;;
            -n|--no-backup)
                CREATE_BACKUP=false
                shift
                ;;
            -r|--rebuild)
                REBUILD_PROJECT=true
                shift
                ;;
            -s|--skip-rebuild)
                REBUILD_PROJECT=false
                shift
                ;;
            -c|--clean-only)
                CLEAN_ONLY=true
                REBUILD_PROJECT=false
                shift
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    # Enable verbose mode if requested
    if [ "$VERBOSE" = true ]; then
        set -x
    fi
    
    print_status "Starting database reset process..."
    print_status "Project root: $PROJECT_ROOT"
    print_status "Database file: $DB_FILE"
    
    # Change to project directory
    cd "$PROJECT_ROOT"
    
    # Stop any running Tomcat instances
    stop_tomcat
    
    # Create backup if requested
    if [ "$CREATE_BACKUP" = true ]; then
        backup_database
    fi
    
    # Remove database file
    remove_database
    
    # Clean target directory
    clean_target
    
    # Rebuild project if requested
    if [ "$REBUILD_PROJECT" = true ] && [ "$CLEAN_ONLY" = false ]; then
        rebuild_project
    fi
    
    # Verify reset if we rebuilt
    if [ "$REBUILD_PROJECT" = true ] && [ "$CLEAN_ONLY" = false ]; then
        verify_reset
    fi
    
    print_success "Database reset completed successfully!"
    
    if [ "$CLEAN_ONLY" = true ]; then
        print_status "Database file removed. Run the application to recreate it."
    else
        print_status "Database has been reset and project rebuilt."
        print_status "You can now start the application with: mvn tomcat7:run"
    fi
}

# Run main function with all arguments
main "$@"

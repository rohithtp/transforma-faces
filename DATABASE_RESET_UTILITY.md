# Database Reset Utility

## Overview

The `reset-db.sh` script is a comprehensive utility designed to reset the local SQLite database for the Transforma-Faces application. This tool provides a safe and reliable way to clean and recreate the database with fresh sample data.

## Features

- 🔄 **Complete Database Reset**: Removes and recreates the SQLite database
- 💾 **Backup Creation**: Optional timestamped backup before reset
- 🛑 **Process Management**: Automatically stops running Tomcat instances
- 🏗️ **Project Rebuild**: Rebuilds the Maven project to recreate database
- ✅ **Verification**: Validates that the database was properly recreated
- 🎛️ **Flexible Options**: Multiple command-line options for different scenarios
- 🎨 **Colored Output**: Clear, color-coded status messages

## Prerequisites

- **Maven**: Required for project rebuilding
- **Java 8+**: Required for running the application
- **SQLite3** (optional): For database verification
- **Bash**: The script requires a bash shell

## Quick Start

### Basic Usage

```bash
# Reset database with backup and rebuild
./reset-db.sh
```

### Common Commands

```bash
# Reset without creating backup
./reset-db.sh --no-backup

# Only clean database, don't rebuild project
./reset-db.sh --clean-only

# Reset but skip project rebuild
./reset-db.sh --skip-rebuild

# Show help and all options
./reset-db.sh --help
```

## Command Line Options

| Option | Long Option | Description |
|--------|-------------|-------------|
| `-h` | `--help` | Show help message and exit |
| `-b` | `--backup` | Create backup before reset (default) |
| `-n` | `--no-backup` | Skip backup creation |
| `-r` | `--rebuild` | Rebuild project after reset (default) |
| `-s` | `--skip-rebuild` | Skip project rebuild |
| `-c` | `--clean-only` | Only clean database, don't rebuild |
| `-v` | `--verbose` | Enable verbose output |

## Usage Scenarios

### 1. Development Reset
When you want to start fresh during development:

```bash
./reset-db.sh
```

**What happens:**
- Stops any running Tomcat instances
- Creates a backup of existing database
- Removes the database file
- Cleans Maven target directory
- Rebuilds the project
- Verifies the new database

### 2. Quick Clean
When you only want to remove the database without rebuilding:

```bash
./reset-db.sh --clean-only
```

**What happens:**
- Stops Tomcat instances
- Removes the database file
- Cleans target directory
- **Does not** rebuild the project

### 3. No Backup Reset
When you're confident and don't need a backup:

```bash
./reset-db.sh --no-backup
```

**What happens:**
- Same as development reset
- **Skips** backup creation

### 4. Skip Rebuild
When you want to clean but rebuild manually:

```bash
./reset-db.sh --skip-rebuild
```

**What happens:**
- Stops Tomcat instances
- Creates backup (if not disabled)
- Removes database file
- Cleans target directory
- **Does not** rebuild the project

## Database Schema

The reset utility recreates the database with the following schema:

```sql
CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    priority TEXT NOT NULL DEFAULT 'Medium',
    status TEXT NOT NULL DEFAULT 'To Do',
    created_date TEXT NOT NULL,
    updated_date TEXT
);
```

## Sample Data

After reset, the database is populated with the following sample tasks:

1. **Complete project setup** (High priority, In Progress)
2. **Design user interface** (Medium priority, To Do)
3. **Implement core functionality** (High priority, To Do)
4. **Write unit tests** (Medium priority, To Do)
5. **Documentation** (Low priority, To Do)

## File Locations

- **Database File**: `transforma_tasks.db` (project root)
- **Backup Files**: `transforma_tasks.db.backup.YYYYMMDD_HHMMSS`
- **Script Location**: `reset-db.sh` (project root)
- **Target Directory**: `target/` (Maven build output)

## Troubleshooting

### Common Issues

#### 1. Permission Denied
```bash
chmod +x reset-db.sh
```

#### 2. Maven Not Found
```bash
# Install Maven or ensure it's in PATH
export PATH=$PATH:/path/to/maven/bin
```

#### 3. Database File Locked
The script automatically stops Tomcat instances, but if you still get lock errors:
```bash
# Manually stop any Java processes using the database
pkill -f "transforma"
```

#### 4. SQLite3 Not Found
This is optional and only affects verification:
```bash
# Install SQLite3 (macOS)
brew install sqlite3

# Install SQLite3 (Ubuntu/Debian)
sudo apt-get install sqlite3
```

### Verbose Mode

For detailed output during execution:

```bash
./reset-db.sh --verbose
```

This will show all commands being executed, useful for debugging.

## Safety Features

### Automatic Backup
By default, the script creates a timestamped backup before resetting:
```
transforma_tasks.db.backup.20241221_143022
```

### Process Management
The script automatically:
- Detects running Tomcat processes
- Attempts graceful shutdown
- Force kills if necessary
- Prevents database file locks

### Error Handling
- Exits on any error (`set -e`)
- Provides clear error messages
- Validates prerequisites
- Verifies successful completion

## Integration with Development Workflow

### Before Committing
```bash
# Reset to clean state
./reset-db.sh --clean-only
# Run tests
mvn test
# Commit changes
git add . && git commit -m "Your changes"
```

### After Pulling Changes
```bash
# Reset database to match new schema
./reset-db.sh
# Start development server
mvn tomcat7:run
```

### Testing Database Changes
```bash
# Reset to known state
./reset-db.sh
# Test your changes
# Reset again if needed
./reset-db.sh --no-backup
```

## Best Practices

1. **Always backup** before major changes (default behavior)
2. **Use clean-only** for quick development iterations
3. **Verify reset** by checking the application after running
4. **Keep backups** for a few days before cleaning up
5. **Use verbose mode** when troubleshooting issues

## Examples

### Complete Development Reset
```bash
# Full reset with backup and rebuild
./reset-db.sh
```

### Quick Iteration
```bash
# Fast clean without backup
./reset-db.sh --no-backup --clean-only
# Make changes
# Rebuild manually
mvn clean compile
```

### Production-like Testing
```bash
# Reset and start server
./reset-db.sh
mvn tomcat7:run
```

### Debugging Issues
```bash
# Verbose output for troubleshooting
./reset-db.sh --verbose --no-backup
```

## Support

If you encounter issues with the database reset utility:

1. Check the prerequisites are installed
2. Run with `--verbose` for detailed output
3. Ensure no other processes are using the database
4. Verify Maven can build the project manually
5. Check file permissions on the script and database file

For additional help, refer to the application's main README.md or contact the development team.

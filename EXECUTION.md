# Execution Guide

This document contains step-by-step instructions for setting up, running, and testing the Apache MyFaces Transforma application.

## 🚀 Quick Start

### Prerequisites

- **Java**: JDK 8 or higher
- **Maven**: 3.6.0 or higher
- **Servlet Container**: Apache Tomcat 9.0+, WildFly, or similar
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Installation & Setup

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd transforma-faces
```

#### 2. Build the Project

```bash
mvn clean install
```

#### 3. Run the Application

```bash
mvn tomcat7:run
```

The application will be available at: `http://localhost:8080/transforma-faces`

**Note**: The Tomcat plugin is now configured in the pom.xml, so this command will work directly.

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TaskBeanTest

# Run with detailed output
mvn test -Dtest=TaskBeanTest -Dsurefire.useFile=false

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

### Test Results Summary

**✅ TaskBeanTest (16 tests)**: All tests passing
- TaskBean creation and initialization
- CRUD operations (Create, Read, Update, Delete)
- Task validation (empty/null titles)
- Task inner class properties
- Mock DAO integration

**✅ TaskDAOTest (8 tests)**: All tests passing
- Task entity properties and validation
- Data integrity testing
- Special character handling
- Long content validation

**✅ LocaleBeanTest (5 tests)**: All tests passing
- Locale management functionality
- Default locale handling
- Locale change operations

**⚠️ TaskManagementIntegrationTest (6 tests)**: 5 failures
- Integration tests require database isolation improvements
- Tests are currently using the real database instead of test database
- Future enhancement: Implement proper test database isolation

## 🔧 Development Commands

### Build Commands

```bash
# Clean and compile
mvn clean compile

# Package the application
mvn package

# Install to local repository
mvn install

# Skip tests during build
mvn clean install -DskipTests
```

### Development Server

```bash
# Run with Tomcat (default)
mvn tomcat7:run

# Run with specific port
mvn tomcat7:run -Dmaven.tomcat.port=9090

# Run in debug mode
mvn tomcat7:run -Dmaven.tomcat.debug=true
```

### Database Operations

```bash
# The SQLite database (transforma_tasks.db) is automatically created on first run
# No additional database setup is required
```

## 🐛 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Kill process using port 8080
   lsof -ti:8080 | xargs kill -9
   ```

2. **Maven Build Failures**
   ```bash
   # Clean and rebuild
   mvn clean install -U
   ```

3. **Database Connection Issues**
   - Ensure the `transforma_tasks.db` file is not locked
   - Check file permissions in the project root directory

4. **JSF Configuration Issues**
   - Verify `faces-config.xml` is properly configured
   - Check managed bean declarations

### Logs and Debugging

- **Application Logs**: Check Tomcat logs in `target/tomcat/logs/`
- **Maven Debug**: Use `-X` flag for detailed Maven output
- **Test Debug**: Use `-Dsurefire.useFile=false` for test output

## 📋 Environment Variables

You can customize the application behavior using these environment variables:

```bash
# Database configuration
export DB_URL=jdbc:sqlite:transforma_tasks.db
export DB_POOL_SIZE=10

# Application configuration
export APP_PORT=8080
export APP_CONTEXT_PATH=/transforma-faces

# Logging configuration
export LOG_LEVEL=INFO
```

## 🔄 Continuous Integration

For CI/CD pipelines, use these commands:

```bash
# CI Build
mvn clean verify

# Deploy to staging
mvn clean package
# Deploy the WAR file to your application server

# Production build
mvn clean package -Pproduction
```

---

**For more detailed information, see the main [README.md](README.md) file.**

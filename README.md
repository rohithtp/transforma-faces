# Apache MyFaces Transforma

A modern Java web application built with Apache MyFaces, providing a robust foundation for JSF-based web development with integrated task management capabilities.

## 🚀 Project Overview

Transforma-Faces is a web application framework that leverages Apache MyFaces to deliver high-performance, scalable JavaServer Faces applications. This project serves as a foundation for building enterprise-grade web applications with modern JSF capabilities and includes a fully functional task management system.

## ✨ Features

- **Apache MyFaces 2.3+**: Stable JSF implementation with enhanced performance
- **Maven-based**: Standardized build and dependency management
- **Modern Web Standards**: HTML5, CSS3, and JavaScript support
- **Responsive Design**: Mobile-first approach with modern UI components
- **Modular Architecture**: Clean separation of concerns and maintainable code structure
- **Task Management System**: Complete CRUD operations for task management
- **SQLite Database**: Lightweight, file-based database with connection pooling
- **Internationalization**: Multi-language support with resource bundles
- **Session Management**: Proper session scoping for managed beans

## 🛠️ Prerequisites

- **Java**: JDK 8 or higher
- **Maven**: 3.6.0 or higher
- **Servlet Container**: Apache Tomcat 9.0+, WildFly, or similar
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

## 📦 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd transforma-faces
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn tomcat7:run
```

The application will be available at: `http://localhost:8080/transforma-faces`

**Note**: The Tomcat plugin is now configured in the pom.xml, so this command will work directly.

## 🏗️ Project Structure

```
transforma-faces/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/apache/myfaces/transforma/
│   │   │       ├── beans/          # Managed beans (TaskBean, LocaleBean)
│   │   │       ├── dao/            # Data Access Objects (TaskDAO)
│   │   │       ├── utils/          # Utility classes (DatabaseUtil)
│   │   │       └── messages.properties # Internationalization resources
│   │   ├── resources/
│   │   │   ├── css/               # Stylesheets
│   │   │   ├── js/                # JavaScript files
│   │   │   └── images/            # Image assets
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── faces-config.xml # JSF configuration and managed beans
│   │       │   └── web.xml        # Web application configuration
│   │       ├── index.xhtml        # Main application page
│   │       └── resources/
│   └── test/
│       ├── java/                  # Test classes
│       └── resources/             # Test resources
├── pom.xml                        # Maven configuration
├── README.md                      # This file
├── transforma_tasks.db            # SQLite database file
└── .gitignore                     # Git ignore rules
```

## 🔧 Configuration

### Maven Dependencies

The project includes essential MyFaces and database dependencies:

```xml
<!-- MyFaces Core -->
<dependency>
    <groupId>org.apache.myfaces.core</groupId>
    <artifactId>myfaces-impl</artifactId>
    <version>2.3.9</version>
</dependency>
<dependency>
    <groupId>org.apache.myfaces.core</groupId>
    <artifactId>myfaces-api</artifactId>
    <version>2.3.9</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.42.0.0</version>
</dependency>
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

### Database Configuration

The application uses SQLite with HikariCP connection pooling:

- **Database File**: `transforma_tasks.db` (auto-created on first run)
- **Connection Pool**: HikariCP with optimized settings
- **Schema**: Auto-initialized with tasks table and sample data

### JSF Configuration

The `faces-config.xml` file configures:
- Managed beans (TaskBean, LocaleBean)
- Resource bundles for internationalization
- Locale configuration
- Navigation rules

## 🚀 Development

### Task Management Features

The application includes a complete task management system:

- **Create Tasks**: Add new tasks with title, description, priority, and status
- **Read Tasks**: Display all tasks in a responsive data table
- **Update Tasks**: Edit task details inline
- **Delete Tasks**: Remove tasks from the system
- **Refresh**: Reload tasks from database

### Adding New Features

1. **Create Managed Beans**: Add new beans in the `beans` package
2. **Data Access Layer**: Implement DAO classes in the `dao` package
3. **Business Logic**: Implement business logic in appropriate service classes
4. **UI Updates**: Modify XHTML files and add corresponding CSS/JS

### Code Style Guidelines

- Follow Java naming conventions
- Use meaningful variable and method names
- Add comprehensive JavaDoc comments
- Implement proper error handling
- Write unit tests for new functionality

### Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

## 🐛 Recent Bug Fixes

### TaskBean Initialization Issue (v1.2.1)

**Problem**: The "no tasks" message was being displayed even when tasks were present in the database.

**Root Cause**: 
- Conflicting bean declarations (both annotation and XML-based)
- TaskBean not being properly initialized
- JSF expression evaluation issues

**Solution**:
- Removed conflicting `@ManagedBean` annotations
- Used XML-based bean configuration in `faces-config.xml`
- Enhanced TaskBean initialization with `ensureTasksInitialized()` method
- Improved getter method to handle null cases

**Files Modified**:
- `src/main/java/org/apache/myfaces/transforma/beans/TaskBean.java`
- `src/main/webapp/WEB-INF/faces-config.xml`
- `src/main/webapp/index.xhtml`

## 📚 Documentation

- [Apache MyFaces Documentation](https://myfaces.apache.org/)
- [JSF Specification](https://jakarta.ee/specifications/faces/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [HikariCP Documentation](https://github.com/brettwooldridge/HikariCP)
- [SQLite Documentation](https://www.sqlite.org/docs.html)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Issues**: Report bugs and feature requests via GitHub Issues
- **Discussions**: Join community discussions on GitHub Discussions
- **Documentation**: Check the project wiki for detailed guides

## 🔄 Version History

- **v1.0.0** - Initial project setup with Apache MyFaces foundation
- **v1.1.0** - Added task management system with SQLite database
- **v1.2.0** - Enhanced UI and added internationalization support
- **v1.2.1** - Fixed TaskBean initialization and JSF expression issues

---

**Built with ❤️ using Apache MyFaces** 

## 🧪 Testing

The project includes comprehensive JUnit tests for all CRUD operations:

### Test Coverage

- **TaskBeanTest**: Unit tests for TaskBean CRUD operations using Mockito
- **TaskDAOTest**: Unit tests for Task entity properties and validation
- **TaskManagementIntegrationTest**: Integration tests for complete task lifecycle
- **LocaleBeanTest**: Unit tests for locale management

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TaskBeanTest

# Run with detailed output
mvn test -Dtest=TaskBeanTest -Dsurefire.useFile=false
```

### Test Dependencies

The project uses the following testing dependencies:

```xml
<!-- JUnit 4 for unit testing -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>

<!-- Mockito for mocking -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>4.5.1</version>
    <scope>test</scope>
</dependency>

<!-- H2 Database for testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.214</version>
    <scope>test</scope>
</dependency>
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

### Test Configuration

Test configuration is managed in `src/test/resources/test-config.properties`:

```properties
# Database Configuration for Testing
test.db.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
test.db.user=sa
test.db.password=

# Test Data Configuration
test.task.default.priority=Medium
test.task.default.status=To Do
test.task.sample.count=5
```

### Testing Best Practices

1. **Unit Tests**: Use Mockito for mocking dependencies
2. **Integration Tests**: Use H2 in-memory database for isolation
3. **Test Data**: Create helper methods for generating test data
4. **Assertions**: Use descriptive assertion messages
5. **Cleanup**: Properly clean up test resources in @After methods

### Future Test Enhancements

- [ ] Implement proper database isolation for integration tests
- [ ] Add performance tests for large datasets
- [ ] Add security tests for input validation
- [ ] Add UI tests using Selenium WebDriver
- [ ] Add API tests for REST endpoints (if implemented) 
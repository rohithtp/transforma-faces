# Apache MyFaces Transforma

A modern Java web application built with Apache MyFaces, showcasing the latest technologies and best practices.

## 🚀 Recent Major Upgrades

This project has been upgraded to use the latest versions of all components and libraries:

### Core Technologies
- **Java**: Upgraded from Java 8 to **Java 17** (Latest LTS)
- **Jakarta EE**: Migrated from Java EE 3.0 to **Jakarta EE 9**
- **JSF**: Upgraded from JSF 2.2 to **JSF 3.0**
- **MyFaces**: Upgraded from MyFaces 2.2.18 to **MyFaces 3.0.2**

### Dependencies
- **Jakarta Servlet**: 6.0.0 (Latest)
- **Jakarta Faces**: 3.0.2 (Latest)
- **Jakarta Expression Language**: 5.0.1 (Latest)
- **Jakarta CDI**: 4.0.1 (Latest)
- **Jakarta Inject**: 2.0.1 (Latest)
- **JUnit**: Upgraded from JUnit 4 to **JUnit 5.10.1**

### Maven Plugins
- **Maven Compiler Plugin**: 3.11.0 (Latest)
- **Maven War Plugin**: 3.4.0 (Latest)
- **Maven Surefire Plugin**: 3.2.2 (Latest)
- **Maven Clean Plugin**: 3.3.2 (Latest)
- **Maven Resources Plugin**: 3.3.1 (Latest)
- **Tomcat Maven Plugin**: 3.0-alpha-1 (Latest)

## 🛠️ Prerequisites

- **Java**: 17 or higher
- **Maven**: 3.6 or higher
- **Application Server**: Tomcat 10+ (Jakarta EE 9 compatible)

## 🚀 Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd transforma-faces
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run with Tomcat Maven Plugin**
   ```bash
   mvn tomcat10:run
   ```

4. **Access the application**
   Open your browser and navigate to: `http://localhost:8080/transforma-faces`

## 🏗️ Project Structure

```
transforma-faces/
├── src/
│   ├── main/
│   │   ├── java/                    # Java source files
│   │   │   └── org/apache/myfaces/transforma/
│   │   │       └── beans/
│   │   │           └── LocaleBean.java
│   │   ├── resources/               # Resource files
│   │   │   └── org/apache/myfaces/transforma/
│   │   │       └── messages.properties
│   │   └── webapp/                  # Web application files
│   │       ├── WEB-INF/
│   │       │   ├── web.xml         # Jakarta EE 9 configuration
│   │       │   ├── faces-config.xml # JSF 3.0 configuration
│   │       │   └── beans.xml       # CDI configuration
│   │       ├── index.xhtml         # Main page
│   │       ├── welcome.xhtml       # Welcome page
│   │       └── documentation.xhtml # Documentation page
│   └── test/                       # Test files
│       └── java/
│           └── org/apache/myfaces/transforma/
│               └── LocaleBeanTest.java
├── pom.xml                         # Maven configuration
└── README.md                       # This file
```

## 🔧 Key Features

- **Modern Architecture**: Built with Jakarta EE 9 and JSF 3.0
- **CDI Integration**: Uses Contexts and Dependency Injection
- **Internationalization**: Multi-language support (English, Spanish, French)
- **Responsive Design**: Modern, mobile-friendly UI
- **Testing**: Comprehensive JUnit 5 test coverage

## 🌐 Internationalization

The application supports multiple languages:
- English (en) - Default
- Spanish (es)
- French (fr)

Language switching is handled by the `LocaleBean` managed bean.

## 🧪 Testing

Run tests with:
```bash
mvn test
```

The project uses JUnit 5 for testing with the latest assertions and testing features.

## 📚 Documentation

- **JSF 3.0**: [Jakarta Faces Specification](https://jakarta.ee/specifications/faces/)
- **MyFaces**: [Apache MyFaces Documentation](https://myfaces.apache.org/)
- **Jakarta EE**: [Jakarta EE Platform](https://jakarta.ee/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the Apache License 2.0.

## 🆘 Support

For issues and questions:
- Check the [documentation](https://myfaces.apache.org/)
- Join the [MyFaces community](https://myfaces.apache.org/community.html)
- Report issues on the project's issue tracker

---

**Built with ❤️ using the latest Jakarta EE and Apache MyFaces technologies** 
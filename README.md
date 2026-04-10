# Online Learning Platform

A microservices-based online learning platform built with Spring Boot and Spring Cloud.

## 🏗️ Architecture

This platform follows a microservices architecture with the following services:

- **configServer**: Centralized configuration service using Spring Cloud Config
- **discoveryService**: Service registry using Netflix Eureka
- **api-gateway**: API Gateway for routing and cross-cutting concerns
- **authService**: Authentication and authorization service (JWT-based)
- **userService**: User management service
- **course-service**: Course catalog and management
- **enrollments**: Student enrollment functionality
- **assessment-service**: Tests, quizzes, and grading system
- **Payment-Service**: Payment processing for course purchases
- **certificateService**: Certificate generation and management

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 4.0.x**
- **Spring Cloud 2025.1.x**
- **Spring Data JPA**
- **MySQL** (database for each service)
- **Eureka** (service discovery)
- **Spring Cloud Config** (centralized configuration)
- **OpenFeign** (declarative REST clients)
- **JWT** (JSON Web Tokens for authentication)
- **Maven** (build and dependency management)

## 📋 Prerequisites

- JDK 17 or higher
- Maven 3.8+
- MySQL server
- Git (for config server if using Git backend)

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone <repository-url>
cd online-learning
```

### 2. Configure MySQL
Create databases for each service (or let them be created automatically):
- configserver
- discovery
- gateway
- auth
- user
- course
- enrollment
- assessment
- payment
- certificate

Update database credentials in respective `application.yml` files or via config server.

### 3. Build the project
```bash
./mvnw clean install
```

### 4. Run the services (in order)

**Terminal 1: Configuration Server**
```bash
./mvnw spring-boot:run -pl configServer
```

**Terminal 2: Discovery Service (Eureka)**
```bash
./mvnw spring-boot:run -pl discoveryService
```

**Terminal 3: API Gateway**
```bash
./mvnw spring-boot:run -pl api-gateway
```

**Terminal 4: Auth Service**
```bash
./mvnw spring-boot:run -pl authService
```

**Terminal 5+: Other Services**
```bash
# User Service
./mvnw spring-boot:run -pl userService

# Course Service
./mvnw spring-boot:run -pl course-service

# Enrollments Service
./mvnw spring-boot:run -pl enrollments

# Assessment Service
./mvnw spring-boot:run -pl assessment-service

# Payment Service
./mvnw spring-boot:run -pl Payment-Service

# Certificate Service
./mvnw spring-boot:run -pl certificateService
```

### 5. Access the platform
- **API Gateway**: http://localhost:8080 (default)
- **Eureka Dashboard**: http://localhost:8761
- **Individual Services**: Check respective `application.yml` for ports

## 🔧 Development

### Building individual services
```bash
./mvnw clean install -pl <service-name> -am
```

### Running tests
```bash
# All tests
./mvnw test

# Specific service
./mvnw test -pl userService

# Specific test class
./mvnw test -Dtest=UserControllerTest -pl userService
```

### Skipping tests (for faster builds)
```bash
./mvnw clean install -DskipTests
```

## 📁 Project Structure

```
online-learning/
├── configServer/          # Configuration service
├── discoveryService/      # Eureka service registry
├── api-gateway/           # API Gateway
├── authService/           # Authentication service
├── userService/           # User management
├── course-service/        # Course management
├── enrollments/           # Enrollment service
├── assessment-service/    # Assessment and grading
├── Payment-Service/       # Payment processing
├── certificateService/    # Certificate generation
├── pom.xml                # Parent Maven project
├── mvnw                   # Maven wrapper
└── mvnw.cmd               # Maven wrapper (Windows)
```

Each service follows a standard Spring Boot structure:
```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/{service}/
│   │   │       ├── {ServiceName}Application.java
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── model/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-{profile}.yml
│   └── test/
│       ├── java/
│   └── resources/
├── pom.xml
└── mvnw / mvnw.cmd
```

## 🔐 Security

- Authentication handled by authService using JWT tokens
- Passwords encoded using BCrypt
- Role-based access control (RBAC)
- API Gateway handles token validation and routing
- Services validate JWT tokens for protected endpoints

## 🔄 Service Communication

- **Service Discovery**: Services register with Eureka (discoveryService)
- **Configuration**: Services fetch configuration from configServer
- **Inter-service Communication**: REST calls via Spring Cloud OpenFeign
- **External Access**: All client traffic goes through api-gateway

## 📝 Configuration

Services use Spring Cloud Config for externalized configuration:
- Default configuration in configServer's native or Git repository
- Environment-specific configurations (application-{profile}.yml)
- Profiles: dev, test, prod (can be extended)

## ⚙️ Environment Variables

Key environment variables that can be configured:
- `SPRING_PROFILES_ACTIVE`: Active Spring profile (dev, test, prod)
- `SERVER_PORT`: Port for the service to run on
- Database connection strings and credentials
- JWT secret keys and expiration times
- Eureka server URL
- Config server URL

## 🐛 Troubleshooting

### Common Issues

1. **Services won't start**:
   - Ensure configServer is running first
   - Ensure discoveryService is running second
   - Check MySQL is running and accessible
   - Verify database credentials in application.yml

2. **Service registration problems**:
   - Check Eureka dashboard (http://localhost:8761)
   - Verify eureka.client.serviceUrl.defaultZone
   - Check network connectivity between services

3. **Database connection errors**:
   - Verify MySQL server is running
   - Check username/password in application.yml
   - Ensure database exists or has create permissions

4. **Port conflicts**:
   - Change server.port in application.yml if needed
   - Check what's running on conflicting ports

### Debugging Commands

```bash
# Check if services are registered
curl http://localhost:8761/eureka/apps

# Test service health (example for user service)
curl http://localhost:8082/actuator/health

# View logs in real-time
./mvnw spring-boot:run -pl userService

# Check OpenFeign client configuration
# Look for @FeignClient annotations in service code
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Spring Boot and Spring Cloud teams
- Netflix OSS for Eureka
- The open source community

---

**Note**: This is a learning project demonstrating microservices architecture with Spring Boot and Spring Cloud. For production use, additional considerations for security, monitoring, logging, and resilience patterns would be needed.
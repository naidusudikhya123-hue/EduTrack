# Edutrack — Online Learning Platform

Microservices-based learning platform: users, courses, enrollments, payments, quizzes, certificates, and JWT-secured API access through a Spring Cloud Gateway.

## Architecture

| Module | Responsibility |
|--------|----------------|
| **configServer** | Central configuration (Spring Cloud Config) |
| **discoveryService** | Eureka service registry |
| **api-gateway** | Routes, JWT validation, role-based path rules |
| **authService** | Signup/login, JWT issuance |
| **userService** | User profiles and roles |
| **course-service** | Courses and videos |
| **enrollments** | Student–course enrollments |
| **Payment-Service** | Paid-course payments, OTP verification, enrollment sync |
| **assessment-service** | Quizzes and attempts |
| **certificateService** | Completion certificates |
| **notification-service** | Kafka-driven notifications (no REST API in codebase) |

**Communication:** REST via OpenFeign between services (Eureka + load balancer); Kafka for course-created and related events.

## Tech stack

- Java 17, Spring Boot 4.x, Spring Cloud 2025.x  
- MySQL (per service), Eureka, Spring Cloud Config  
- Spring Cloud Gateway (WebFlux), OpenFeign, JWT (JJWT)  
- Kafka (course-service, Payment-Service, notification-service)

## Prerequisites

- JDK 17, Maven  
- MySQL (databases per `config-repo/*.yml`)  
- **Recommended startup order:** `configServer` → `discoveryService` → remaining services (any order after registry is up)  
- API Gateway default port in config: **9000** (see `config-repo/api-gateway.yml`)

## API Gateway

- **Base URL (typical):** `http://localhost:9000`  
- **Auth:** `Authorization: Bearer <token>` for protected routes (except public paths such as `/auth/**`, `/actuator/**`).  
- **Routed prefixes:** `/auth`, `/users`, `/enrollments`, `/courses`, `/certificates`, `/quizzes`, `/payments`  

> **Note:** `course-service` also exposes `/videos/**`, but the gateway snippet in `config-repo/api-gateway.yml` only predicates `/courses/**`. To reach videos through the gateway, add a route for `/videos/**` or call **course-service** directly on its configured port.

---

## HTTP API — all endpoints

Paths below are **relative** (prepend gateway base URL or the service’s own `server.port` when calling directly).

### authService — `/auth`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/auth/signup` | Register auth user + create user profile (calls user-service) |
| POST | `/auth/login` | Login; returns JWT |

### userService — `/users`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/users` | Create user |
| GET | `/users` | List all users |
| GET | `/users/{userId}` | Get user by id |
| PUT | `/users/{userId}` | Update user |
| DELETE | `/users/{userId}` | Delete user |
| GET | `/users/role/{roleName}` | Users by role name |

### course-service — `/courses`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/courses/create` | Create course |
| GET | `/courses/get` | List all courses |
| GET | `/courses/{courseId}` | Get course by id |
| PUT | `/courses/{courseId}` | Update course |
| DELETE | `/courses/{courseId}` | Delete course |
| GET | `/courses/instructors/{instructorId}` | Courses by instructor |

### course-service — `/videos`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/videos` | Create video |
| GET | `/videos/get` | List all videos |
| GET | `/videos/getd/{courseId}` | Videos for course |
| PUT | `/videos/{videoId}` | Update video |
| DELETE | `/videos/{videoId}` | Delete video |
| GET | `/videos/count/{courseId}` | Video count for course |

### enrollments — `/enrollments`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/enrollments` | Create enrollment (validates user + course via Feign) |
| GET | `/enrollments/{id}` | Get enrollment by id |
| GET | `/enrollments/users/{userId}/details` | User’s enrollments with profile info |
| GET | `/enrollments/course/{courseId}` | Enrollments for a course |
| PUT | `/enrollments/{id}/cancel` | Cancel enrollment |

### Payment-Service — `/payments`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/payments/addPayment` | Start payment (validates user + course) |
| POST | `/payments/enroll` | Check payment eligibility for enrollment |
| POST | `/payments/verify` | Verify OTP; on success creates enrollment via enrollments service |
| GET | `/payments/user/{userId}` | List payments for user |

### assessment-service — `/quizzes`

| Method | Path | Description |
|--------|------|-------------|
| GET | `/quizzes/start/{courseId}` | Start quiz for course (validates course via Feign) |
| POST | `/quizzes/submit/{quizId}` | Submit answers (validates course via Feign) |
| GET | `/quizzes/users/{userId}` | Quiz attempts + user info for user |

### certificateService — `/certificates`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/certificates/generate` | Issue certificate (validates user, course, active enrollment) |
| GET | `/certificates/{userId}/{courseId}` | Get certificate |

### Infrastructure (typical defaults)

| Service | Purpose | URL |
|---------|---------|-----|
| Eureka | Dashboard | `http://localhost:8761` |
| Config Server | Config API | `http://localhost:8888` (if enabled) |

Actuator endpoints may be exposed per service configuration (e.g. `/actuator/health`).

---

## Inter-service calls (Feign) — reference

These are **not** gateway paths; services call each other by Eureka name.

| Caller | Target | Method | Path |
|--------|--------|--------|------|
| authService | user-service | POST | `/users` |
| enrollments | user-service | GET | `/users/{userId}` |
| enrollments | course-service | GET | `/courses/{courseId}` |
| Payment-Service | user-service | GET | `/users/{userId}` |
| Payment-Service | course-service | GET | `/courses/{courseId}` |
| Payment-Service | enrollments | POST | `/enrollments` |
| assessment-service | user-service | GET | `/users/{userId}` |
| assessment-service | course-service | GET | `/courses/{courseId}` |
| certificateService | user-service | GET | `/users/{userId}` |
| certificateService | course-service | GET | `/courses/{courseId}` |
| certificateService | enrollments | GET | `/enrollments/users/{userId}/details` |

---

## Build & test

```bash
./mvnw clean install
./mvnw clean install -DskipTests
./mvnw test -pl userService
./mvnw spring-boot:run -pl api-gateway
```

Individual service ports and JDBC URLs are defined under `config-repo/` and each module’s `application.yml` (with optional Config Server import).

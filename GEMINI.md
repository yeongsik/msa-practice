# GEMINI.md

This file serves as a context guide for Gemini agents working on this project. It outlines the project structure, build processes, and development standards.

## 1. Project Overview

**Project Name:** MSA Practice
**Description:** A multi-module Spring Boot Microservices Architecture (MSA) practice project implementing a User Service and a Board Service with a shared Common module.
**Key Technologies:**
-   **Java:** 17
-   **Framework:** Spring Boot 4.0.0 (Spring Data JPA, Spring WebMVC)
-   **Build Tool:** Gradle
-   **Database:** MySQL 8.0 (via Docker Compose)
-   **Authentication:** JWT (jjwt 0.11.5)
-   **Utilities:** Lombok

## 2. Architecture & Modules

The project is structured as a Gradle multi-module project:

```
msa-practice/ (Root)
├── common/          # Shared library (DTOs, Utils, Security, Entities) - Not executable
├── user-service/    # User management microservice (Port: 8080 default)
├── board-service/   # Board/content management microservice (Port: 8081 default)
├── build.gradle     # Root build configuration
└── docker-compose.yml # Infrastructure setup (MySQL databases)
```

### Module Details
-   **common**: Contains shared components to ensure consistency across microservices.
    -   `com.common.dto.ApiResponse`: Standardized API response format.
    -   `com.common.entity.BaseTimeEntity`: Base class for entities to automatically handle `createdAt` and `updatedAt`.
    -   `com.common.util.JwtUtil`: JWT token generation and validation.
    -   `com.common.exception.BaseExceptionHandler`: Shared logic for handling common exceptions (Validation, Internal Server Error).
-   **user-service**: Handles user registration, login, and profile management. Dependent on `common`.
-   **board-service**: Handles board posts CRUD operations. Dependent on `common`.

## 3. Development Standards

### API Standards
-   All controller methods must return `ResponseEntity<ApiResponse<T>>`.
-   Use `ApiResponse.success(data)` for successful responses.
-   Use `ApiResponse.error(message)` for error responses (handled via `GlobalExceptionHandler`).

### Entity Standards
-   All JPA entities should extend `BaseTimeEntity` to maintain consistent audit logs.
-   Enable JPA Auditing in each service application class using `@EnableJpaAuditing`.

### Git Branch Strategy
*   **Main Branch**: `master` (Always deployable)
*   **Feature Branches**: `feature/<scope>/<description>` (e.g., `feature/user-service/login`)
*   **Bug Fixes**: `fix/<scope>/<description>`
*   **Process**: Create branch -> Develop -> Pull Request -> Merge to `master`.

### Commit Convention
Format: `<type>(<scope>): <subject>`
*   **Types**: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`.
*   **Scopes**: `user-service`, `board-service`, `common`, `docker`, `gradle`.
*   **Example**: `feat(user-service): Add login endpoint`

## 4. Getting Started
...
## 5. Implementation Status & Roadmap

Refer to `REQUIREMENTS.md` for detailed functional requirements.

### Phase 1: User Service & Infrastructure
-   [x] Project Structure Setup
-   [x] Database Infrastructure (MySQL Docker)
-   [x] Common Module Refactoring (ApiResponse, BaseTimeEntity)
-   [x] User Signup & Login (JWT)
-   [x] JWT Authentication Filter & Security Config
-   [ ] User Profile Management (My Info, Update)

### Phase 2: Board Service
-   [ ] Database Configuration
-   [ ] Board CRUD
-   [ ] Auth Integration (using `common`)

### Phase 3: Integration
-   [ ] Service-to-Service Communication
-   [ ] API Gateway (Optional)

# GEMINI.md

This file serves as a context guide for Gemini agents working on this project. It outlines the project structure, build processes, and development standards.

## 1. Project Overview

**Project Name:** MSA Practice
**Description:** A multi-module Spring Boot Microservices Architecture (MSA) practice project implementing a User Service and a Board Service with a shared Common module and infrastructure services.
**Key Technologies:**
-   **Java:** 17
-   **Framework:** Spring Boot 4.0.0 (Spring Data JPA, Spring WebMVC)
-   **Infrastructure:** Spring Cloud (Netflix Eureka, OpenFeign, Gateway)
-   **Build Tool:** Gradle
-   **Database:** MySQL 8.0 (via Docker Compose)
-   **Containerization:** Docker & Docker Compose

## 2. Architecture & Modules

The project is structured as a Gradle multi-module project:

```
msa-practice/ (Root)
├── common/             # Shared library (DTOs, Utils, Security, Entities)
├── discovery-service/  # Service Discovery (Eureka Server) [Port: 8761]
├── gateway-service/    # API Gateway (Auth, Logging, Routing) [Port: 8000]
├── user-service/       # User management microservice [Port: 8080]
├── board-service/      # Board/content management microservice [Port: 8081]
├── build.gradle        # Root build configuration
└── docker-compose.yml  # Infrastructure & Service orchestration
```

### Module Details
-   **common**: Shared components used across all microservices.
    -   `com.common.dto.ApiResponse`: Standardized API response format.
    -   `com.common.security.JwtAuthenticationFilter`: Shared JWT validation filter.
-   **discovery-service**: Eureka Server for service registration and discovery.
-   **gateway-service**: The entry point for all requests. Handles JWT validation (AuthorizationHeaderFilter), global logging, and routing via `lb://`.
-   **user-service**: Handles authentication, registration, and user profiles.
-   **board-service**: Handles board CRUD. Communicates with `user-service` via OpenFeign.

## 3. Development Standards

### API & Security Standards
-   **Entry Point**: All client requests should go through Gateway (`localhost:8000`).
-   **Response Format**: All controllers must return `ResponseEntity<ApiResponse<T>>`.
-   **Authentication**: Gateway validates JWT and forwards `X-User-Id` header to downstream services. Services can access this via `@AuthenticationPrincipal Long userId`.

### Entity & JPA Standards
-   **Auditing**: Entities should extend `BaseTimeEntity` from the common module.
-   **ID Generation**: Use Snowflake algorithm for distributed ID generation. (See `SnowflakeIdentifierGenerator` in common module)
-   **Configuration**: Enable `@EnableJpaAuditing` and `@EnableDiscoveryClient` in each service application class.

### Code Style
-   **Checkstyle**: Adhere to the project's coding standards defined in `config/checkstyle/checkstyle.xml`. Ensure your IDE is configured to use this file.

## 4. Getting Started

### Prerequisites
-   JDK 17
-   Docker & Docker Compose

### Running with Docker (Recommended)
You can launch the entire MSA environment with a single command:
```bash
docker-compose up -d --build
```
This starts:
-   **Databases**: User DB (3306), Board DB (3307)
-   **Infrastructure**: Eureka (8761), Gateway (8000), Config Server (8888)
-   **Services**: User (8080), Board (8081)

### Local Development (Manual)
1.  Start MySQL containers: `docker-compose up -d user-mysql board-mysql`
2.  Start `discovery-service` first.
3.  Start `config-service`.
4.  Start other services in any order.

## 5. Service Endpoints (via Gateway: 8000)

| Feature | Method | Endpoint | Note |
| :--- | :--- | :--- | :--- |
| **Signup** | POST | `/api/users/signup` | Public |
| **Login** | POST | `/api/users/login` | Public |
| **My Info** | GET | `/api/users/me` | Requires JWT |
| **User Info**| GET | `/api/users/{id}` | Internal/Public (for Feign) |
| **Upload Image**| POST | `/api/users/{id}/profile-image` | Multi-part, JWT Required |
| **Delete Image**| DELETE | `/api/users/{id}/profile-image` | JWT Required |
| **Board List**| GET | `/api/boards` | Requires JWT |
| **Post Board**| POST | `/api/boards` | Requires JWT |
| **Discovery** | UI | `http://localhost:8761` | Eureka Dashboard |
| **Config** | UI | `http://localhost:8888/{service}/{profile}` | Config Server |

## 6. Implementation Status

### Phase 1 & 2: Core Infrastructure & Features (Completed)
-   [x] Common Module (ApiResponse, BaseTimeEntity, Shared JWT Filter)
-   [x] User Service (Auth, Profile, JWT 발급)
-   [x] Board Service (CRUD, User 연동 via Feign)
-   [x] API Gateway (Global Auth, Logging, lb:// Routing)
-   [x] Service Discovery (Eureka Server/Client)
-   [x] Dockerization (Full Stack Orchestration)

### Phase 3: Advanced Patterns (Completed & Ongoing)
-   [x] Distributed ID Generation (Snowflake Algorithm)
-   [x] Centralized Configuration (Spring Cloud Config)
-   [x] Profile Image Management (Thumbnailator, Apache Tika)
-   [ ] Circuit Breaker (Resilience4j)
-   [ ] Distributed Tracing (Zipkin/Sleuth) - Infrastructure ready, need instrumentation.
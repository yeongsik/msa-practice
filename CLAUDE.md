# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 4.0.0 multi-module microservices architecture (MSA) practice project using Gradle. Java 17 is required.

## Build Commands

```bash
# Build entire project
./gradlew build

# Build specific module
./gradlew :user-service:build
./gradlew :board-service:build
./gradlew :common:build

# Run tests
./gradlew test

# Run single module tests
./gradlew :user-service:test
./gradlew :board-service:test

# Run a service
./gradlew :user-service:bootRun
./gradlew :board-service:bootRun
```

## Architecture

```
msa-practice/
├── common/          # Shared library (non-executable) - JWT utilities
├── user-service/    # User management microservice
├── board-service/   # Board/content management microservice
└── build.gradle     # Root config applying plugins to all subprojects
```

### Module Relationships

- **common**: Shared library module (`bootJar` disabled, `jar` enabled). Contains `JwtUtil` for JWT token generation/validation. Not runnable.
- **user-service** and **board-service**: Independent Spring Boot applications with JPA, WebMVC, MySQL, and Lombok dependencies.

### Technology Stack

- Spring Boot 4.0.0 with Spring Data JPA and Spring WebMVC
- MySQL (mysql-connector-j)
- JWT authentication (jjwt 0.11.5)
- Lombok for boilerplate reduction
- JUnit 5 for testing

### Package Structure

- `com.userservice` - User service application
- `com.boardservice` - Board service application
- `com.common.util` - Shared utilities (JwtUtil)

## Code Style

This project enforces **Google Java Style** via Checkstyle. See `config/checkstyle/checkstyle.xml` for detailed rules.

Key points:
- Import order: `java.*` / `javax.*` first, then third-party packages (lombok, spring, etc.)
- Line length: max 120 characters
- Javadoc required on public methods, must end with period (`.`)
- Run checkstyle: `./gradlew checkstyleMain` or `./gradlew build`

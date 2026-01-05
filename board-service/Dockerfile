# ===================================
# Board Service Dockerfile
# ===================================
# Multi-stage build로 이미지 크기 최적화

# Stage 1: Build stage
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Gradle wrapper와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 의존성 다운로드 (캐싱 활용)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY common common
COPY board-service board-service

# 빌드 실행
RUN ./gradlew :board-service:bootJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 보안을 위해 non-root 사용자 생성
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 빌드된 JAR 파일 복사
COPY --from=builder /app/board-service/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# 애플리케이션 실행
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", \
            "app.jar"]
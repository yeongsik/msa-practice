# User Service

MSA 기반 X(구 Twitter) 클론 프로젝트의 사용자 관리 마이크로서비스입니다.

## 🏗 아키텍처

### Domain-Driven Design (DDD) + Hexagonal Architecture
```
├── Domain Layer
│   ├── Models (User, Follow, Profile, Email, Password)
│   ├── Services (UserFollowService, UserValidationService)
│   └── Repository Interfaces
├── Infrastructure Layer
│   ├── Persistence (JPA/Hibernate)
│   ├── Cache (Redis)
│   ├── Messaging (Kafka)
│   └── Security (JWT)
└── Presentation Layer
    ├── REST APIs
    ├── Exception Handling
    └── Configuration
```

## 🚀 주요 기능

### 👤 사용자 관리
- **사용자 등록**: 이메일 검증, 비밀번호 보안 정책 적용
- **프로필 관리**: 사용자 정보 업데이트 및 프로필 이미지 관리
- **계정 보안**: 비밀번호 암호화, JWT 토큰 기반 인증

### 👥 팔로우 시스템
- **팔로우/언팔로우**: 사용자 간 팔로우 관계 관리
- **팔로워/팔로잉 조회**: 실시간 통계 제공
- **상호 팔로우 감지**: 맞팔로우 상태 확인
- **도메인 이벤트 발행**: 팔로우 변경 시 이벤트 스트리밍

### 📊 실시간 통계
- **Redis 캐싱**: 팔로워/팔로잉 수 실시간 업데이트
- **이벤트 기반 업데이트**: Kafka를 통한 비동기 통계 갱신

### 🔐 보안 시스템
- **JWT 인증**: Access/Refresh 토큰 관리
- **Spring Security**: 엔드포인트 보안
- **비밀번호 정책**: 강력한 비밀번호 요구사항

## 🛠 기술 스택

### Core Framework
- **Spring Boot**: 3.5.3
- **Java**: 22 (Toolchain)
- **Spring Security**: JWT 기반 인증
- **Spring Data JPA**: 데이터 접근 계층

### 데이터베이스
- **PostgreSQL**: 운영 환경 메인 DB
- **H2**: 개발/테스트 환경 인메모리 DB
- **Redis**: 사용자 통계 캐싱

### 메시징 & 이벤트
- **Apache Kafka**: 이벤트 스트리밍
- **Spring Events**: 도메인 이벤트 처리

### 보안 & 인증
- **JWT (JSON Web Token)**: 0.11.5
- **BCrypt**: 비밀번호 암호화
- **Spring Security**: 인증/인가

### 모니터링 & 운영
- **Spring Actuator**: 헬스체크 및 메트릭
- **Lombok**: 코드 간소화

## 🏛 도메인 모델

### 핵심 엔티티
```java
User (Aggregate Root)
├── UserId (Snowflake ID)
├── Profile (이름, 바이오, 프로필 이미지)
├── Email (검증된 이메일)
├── Username (고유 사용자명)
├── Password (암호화된 비밀번호)
└── Timestamps (생성/수정 시간)

Follow
├── FollowId (복합키)
├── FollowerId (팔로우 하는 사용자)
├── FolloweeId (팔로우 당하는 사용자)
└── Timestamps
```

### Value Objects
- **UserId, FollowId**: Snowflake 기반 분산 ID
- **Email**: RFC 표준 이메일 검증
- **Username**: 형식 검증 및 고유성 보장
- **Password**: 암호화 및 강도 검증

## 🔄 이벤트 플로우

### 팔로우 이벤트
```
1. 사용자가 팔로우 요청
   ↓
2. 도메인 검증 (중복, 권한 등)
   ↓
3. Follow 엔티티 생성
   ↓
4. FollowCreatedEvent 발행
   ↓
5. Kafka로 이벤트 전송
   ↓
6. Redis 통계 캐시 업데이트
```

### 언팔로우 이벤트
```
1. 사용자가 언팔로우 요청
   ↓
2. Follow 엔티티 소프트 삭제
   ↓
3. FollowRemovedEvent 발행
   ↓
4. Kafka로 이벤트 전송
   ↓
5. Redis 통계 캐시 업데이트
```

## 🗄 데이터베이스 설계

### 주요 테이블
- **users**: 사용자 기본 정보
- **follows**: 팔로우 관계 (복합 PK)
- **Indexes**: 성능 최적화를 위한 인덱스 설계

### 캐시 전략
```
Redis Keys:
- user:following:count:{userId}
- user:follower:count:{userId}
TTL: 24시간 (자동 갱신)
```

## 📦 실행 방법

### 사전 요구사항
- **Java 22** 이상
- **PostgreSQL** (운영 환경)
- **Redis** (캐시)
- **Apache Kafka** (이벤트 스트리밍)

### 로컬 실행
```bash
# 의존성 다운로드 및 빌드
./gradlew build

# 개발 환경 실행 (H2 DB 사용)
./gradlew bootRun --args='--spring.profiles.active=local'

# 운영 환경 실행
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### Docker 실행 (계획)
```bash
# Docker 이미지 빌드
docker build -t user-service .

# 컨테이너 실행
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod user-service
```

## ⚙️ 설정 관리

### Profile별 설정
- **local**: H2, 임베디드 Redis, 로컬 Kafka
- **dev**: PostgreSQL, Redis, 개발 Kafka 클러스터
- **staging**: 운영과 유사한 환경
- **prod**: 운영 환경 (모든 외부 시스템 연동)

### 환경 변수
```bash
# 데이터베이스
DATABASE_URL=jdbc:postgresql://localhost:5432/userservice
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# JWT
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=900000
```

## 📊 모니터링

### Actuator Endpoints
- `/actuator/health`: 서비스 상태 확인
- `/actuator/info`: 애플리케이션 정보
- `/actuator/metrics`: 성능 메트릭

### 로깅
- **구조화된 로깅**: JSON 형태의 로그 출력
- **레벨별 설정**: 환경별 로그 레벨 조정
- **보안 로깅**: 민감정보 마스킹

## 🧪 테스트

### 테스트 전략
```bash
# 단위 테스트
./gradlew test

# 통합 테스트
./gradlew integrationTest

# 테스트 커버리지
./gradlew jacocoTestReport
```

### 테스트 데이터
- **@TestProfile**: 테스트 전용 설정
- **@DataJpaTest**: JPA 계층 테스트
- **@WebMvcTest**: API 계층 테스트

## 🚧 향후 계획

### 단기 계획
- [ ] REST API 엔드포인트 구현
- [ ] API 문서 자동화 (Swagger/OpenAPI)
- [ ] 통합 테스트 확장
- [ ] Docker 컨테이너화

### 장기 계획
- [ ] GraphQL API 지원
- [ ] 이벤트 소싱 도입
- [ ] CQRS 패턴 적용
- [ ] 마이크로서비스 오케스트레이션
- [ ] 분산 트레이싱 (Zipkin, Jaeger)
- [ ] Circuit Breaker 패턴

## 🏗 개발 가이드

### 코딩 컨벤션
- **Google Java Style Guide** 준수
- **Domain-First** 접근법
- **Test-Driven Development** 권장
- **Clean Code** 원칙 적용

### Git 워크플로우
- **Feature Branch** 전략
- **Conventional Commits** 메시지
- **코드 리뷰** 필수

## 📄 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
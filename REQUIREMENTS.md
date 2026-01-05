# MSA Practice Project - 기능 요구사항 및 작업 계획

## 📋 기능 요구사항

### 1. User Service (사용자 관리)
- **회원가입** - 사용자 정보 등록
- **로그인** - JWT 토큰 발급
- **사용자 조회** - 사용자 정보 조회 (자신/타인)
- **사용자 수정** - 프로필 수정
- **인증/인가** - JWT 기반 인증

### 2. Board Service (게시판 관리)
- **게시글 작성** - 인증된 사용자가 글 작성
- **게시글 조회** - 전체 목록, 단건 조회
- **게시글 수정** - 작성자 본인만 가능
- **게시글 삭제** - 작성자 본인만 가능
- **사용자 정보 조회** - User Service 호출 (Service-to-Service 통신)

### 3. Common Module
- **JWT 유틸리티** - 토큰 생성/검증 (✅ 구현됨)
- **공통 DTO** - 응답 포맷, 예외 처리
- **공통 인터셉터** - JWT 검증 로직

---

## 🚀 작업 계획

### Phase 1: User Service 기본 구현
1. **데이터베이스 설정**
   - MySQL 연결 설정 (application.properties)
   - User 엔티티 설계 (id, username, password, email, createdAt)

2. **회원가입/로그인 구현**
   - UserRepository (JPA)
   - UserService (비즈니스 로직, 비밀번호 암호화)
   - UserController (회원가입, 로그인 API)
   - DTO 클래스 (SignUpRequest, LoginRequest, LoginResponse)

3. **사용자 조회 API**
   - 사용자 정보 조회 엔드포인트
   - JWT 인증 필터/인터셉터 적용

### Phase 2: Board Service 기본 구현
1. **데이터베이스 설정**
   - MySQL 연결 설정
   - Board 엔티티 설계 (id, title, content, userId, createdAt, updatedAt)

2. **게시판 CRUD 구현**
   - BoardRepository (JPA)
   - BoardService (비즈니스 로직)
   - BoardController (게시글 CRUD API)
   - DTO 클래스 (CreateBoardRequest, BoardResponse)

3. **인증 통합**
   - JWT 검증 필터 추가
   - 작성자 권한 검증 로직

### Phase 3: MSA 핵심 패턴 적용
1. **Service-to-Service 통신**
   - RestTemplate 또는 WebClient 설정
   - Board Service에서 User Service 호출
   - 게시글 조회 시 작성자 정보 포함

2. **공통 모듈 확장**
   - 공통 Response DTO (성공/실패 응답 포맷)
   - 공통 예외 처리 (@RestControllerAdvice)
   - JWT 인터셉터를 common으로 이동

3. **API Gateway 패턴 (선택)**
   - Spring Cloud Gateway 추가
   - 라우팅 설정 (user-service, board-service)
   - JWT 검증을 Gateway에서 처리

### Phase 4: 고급 MSA 패턴 (선택)
1. **서비스 디스커버리**
   - Eureka Server 설정
   - 각 서비스 Eureka Client 등록

2. **설정 중앙화**
   - Spring Cloud Config Server
   - 외부 설정 관리

3. **분산 추적**
   - Sleuth + Zipkin 통합
   - 요청 추적 로그

4. **서킷 브레이커**
   - Resilience4j 적용
   - 서비스 장애 격리

---

## 📝 API 명세 (예정)

### User Service APIs
```
POST   /api/users/signup      # 회원가입
POST   /api/users/login       # 로그인
GET    /api/users/{id}        # 사용자 조회
PUT    /api/users/{id}        # 사용자 수정
```

### Board Service APIs
```
POST   /api/boards            # 게시글 작성
GET    /api/boards            # 게시글 목록 조회
GET    /api/boards/{id}       # 게시글 단건 조회
PUT    /api/boards/{id}       # 게시글 수정
DELETE /api/boards/{id}       # 게시글 삭제
```

---

## 🗄️ 데이터베이스 스키마 (예정)

### User Service - users 테이블
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Board Service - boards 테이블
```sql
CREATE TABLE boards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 🔧 기술 스택

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 17
- **Build Tool**: Gradle
- **Database**: MySQL
- **ORM**: Spring Data JPA
- **Security**: JWT (jjwt 0.11.5)
- **Utilities**: Lombok

---

## 📚 학습 목표

이 프로젝트를 통해 다음을 학습할 수 있습니다:
- ✅ 멀티 모듈 프로젝트 구조
- ✅ 마이크로서비스 간 독립적 배포
- ✅ JWT 기반 인증/인가
- ✅ Service-to-Service 통신
- ✅ 공통 모듈 설계 및 재사용
- 🔄 API Gateway 패턴
- 🔄 서비스 디스커버리
- 🔄 분산 추적 및 모니터링
- 🔄 장애 격리 및 복원력

---

## 🎯 다음 작업

즉시 시작할 수 있는 작업:
1. User Service 데이터베이스 설정
2. 회원가입/로그인 API 구현
3. Board Service 설정 및 CRUD 구현

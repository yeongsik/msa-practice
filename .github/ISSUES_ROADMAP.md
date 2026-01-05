# GitHub Issues 로드맵

## 📋 Issue 생성 순서

### 🎯 Phase 0: 인프라 설정 (완료)
- [x] #0 Docker 환경 설정 및 CI/CD 구축

---

### 🎯 Phase 1: User Service 기본 구현

#### Issue #1: User Service 프로젝트 초기 설정
**Labels**: `user-service`, `setup`, `phase-1`
**Assignee**: yourself
**Milestone**: Phase 1 - User Service

**Description**:
```markdown
## 📝 작업 내용
User Service 프로젝트 기본 설정 및 데이터베이스 연결

## ✅ 체크리스트
- [ ] application.properties 설정
  - [ ] MySQL 연결 정보 (localhost:3306/userdb)
  - [ ] JPA 설정 (ddl-auto, show-sql)
  - [ ] Logging 설정
- [ ] build.gradle 의존성 확인
  - [ ] Spring Web
  - [ ] Spring Data JPA
  - [ ] MySQL Driver
  - [ ] Lombok
  - [ ] common 모듈 의존성
- [ ] Application 클래스 생성
- [ ] 서버 실행 테스트 (Port 8080)

## 🧪 테스트 방법
```bash
./gradlew :user-service:bootRun
# http://localhost:8080 접속 확인
```

## 📚 참고 자료
- REQUIREMENTS.md: Phase 1-1
- CLAUDE.md: 프로젝트 구조
```

---

#### Issue #2: User 엔티티 및 Repository 구현
**Labels**: `user-service`, `feature`, `phase-1`
**Depends on**: #1

**Description**:
```markdown
## 📝 작업 내용
사용자 정보를 저장할 User 엔티티 및 Repository 구현

## ✅ 체크리스트
- [ ] User 엔티티 클래스 생성
  - [ ] id (Long, @GeneratedValue)
  - [ ] username (String, unique)
  - [ ] password (String, 암호화된 비밀번호)
  - [ ] email (String, unique)
  - [ ] createdAt (LocalDateTime)
  - [ ] @Entity, @Table 어노테이션
  - [ ] Lombok (@Getter, @NoArgsConstructor, @Builder)
- [ ] UserRepository 인터페이스 생성
  - [ ] JpaRepository 상속
  - [ ] findByUsername(String username) 메서드
  - [ ] findByEmail(String email) 메서드
  - [ ] existsByUsername(String username) 메서드
- [ ] 테이블 자동 생성 확인 (MySQL)

## 🧪 테스트 방법
```bash
# MySQL 접속
docker exec -it user-mysql mysql -uroot -proot userdb

# 테이블 확인
SHOW TABLES;
DESC users;
```

## 📚 참고 자료
- REQUIREMENTS.md: 데이터베이스 스키마
```

---

#### Issue #3: 회원가입 API 구현
**Labels**: `user-service`, `feature`, `api`, `phase-1`
**Depends on**: #2

**Description**:
```markdown
## 📝 작업 내용
사용자 회원가입 API 구현 (POST /api/users/signup)

## ✅ 체크리스트
- [ ] DTO 클래스 생성
  - [ ] SignUpRequest (username, password, email)
  - [ ] UserResponse (id, username, email, createdAt)
- [ ] UserService 구현
  - [ ] 중복 사용자 검증 (username, email)
  - [ ] 비밀번호 암호화 (BCryptPasswordEncoder)
  - [ ] 사용자 저장
- [ ] UserController 구현
  - [ ] POST /api/users/signup 엔드포인트
  - [ ] @Valid 검증
  - [ ] 성공 응답: 201 Created
  - [ ] 실패 응답: 400 Bad Request
- [ ] 예외 처리
  - [ ] DuplicateUsernameException
  - [ ] DuplicateEmailException
- [ ] 통합 테스트 작성

## 🧪 테스트 방법
```bash
# 회원가입 요청
curl -X POST http://localhost:8080/api/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com"
  }'

# 기대 응답: 201 Created
# {
#   "id": 1,
#   "username": "testuser",
#   "email": "test@example.com",
#   "createdAt": "2024-11-29T..."
# }
```

## 📚 참고 자료
- REQUIREMENTS.md: API 명세
```

---

#### Issue #4: 로그인 API 및 JWT 토큰 발급 구현
**Labels**: `user-service`, `feature`, `api`, `security`, `phase-1`
**Depends on**: #3

**Description**:
```markdown
## 📝 작업 내용
로그인 API 구현 및 JWT 토큰 발급 (POST /api/users/login)

## ✅ 체크리스트
- [ ] DTO 클래스 생성
  - [ ] LoginRequest (username, password)
  - [ ] LoginResponse (token, username, expiresIn)
- [ ] UserService 로그인 로직
  - [ ] 사용자 존재 여부 확인
  - [ ] 비밀번호 검증 (BCrypt)
  - [ ] JWT 토큰 생성 (common 모듈의 JwtUtil 활용)
- [ ] UserController 구현
  - [ ] POST /api/users/login 엔드포인트
  - [ ] 성공 응답: 200 OK + JWT 토큰
  - [ ] 실패 응답: 401 Unauthorized
- [ ] 예외 처리
  - [ ] InvalidCredentialsException
- [ ] 통합 테스트 작성

## 🧪 테스트 방법
```bash
# 로그인 요청
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 기대 응답: 200 OK
# {
#   "token": "eyJhbGciOiJIUzI1NiIs...",
#   "username": "testuser",
#   "expiresIn": 3600
# }
```

## 📚 참고 자료
- common/JwtUtil.java
- REQUIREMENTS.md: JWT 기반 인증
```

---

#### Issue #5: 사용자 조회 API 및 JWT 인증 구현
**Labels**: `user-service`, `feature`, `api`, `security`, `phase-1`
**Depends on**: #4

**Description**:
```markdown
## 📝 작업 내용
JWT 인증이 필요한 사용자 조회 API 구현 (GET /api/users/{id})

## ✅ 체크리스트
- [ ] JWT 인증 필터/인터셉터 구현
  - [ ] Authorization 헤더에서 토큰 추출
  - [ ] 토큰 유효성 검증 (JwtUtil)
  - [ ] 인증 실패 시 401 응답
- [ ] UserService 조회 로직
  - [ ] ID로 사용자 조회
  - [ ] 존재하지 않으면 404
- [ ] UserController 구현
  - [ ] GET /api/users/{id} 엔드포인트
  - [ ] JWT 인증 필수
- [ ] 통합 테스트 작성
  - [ ] 토큰 없이 요청 → 401
  - [ ] 유효한 토큰 → 200

## 🧪 테스트 방법
```bash
# 1. 로그인하여 토큰 획득
TOKEN=$(curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' \
  | jq -r '.token')

# 2. 사용자 조회 (인증 필요)
curl http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"

# 기대 응답: 200 OK + 사용자 정보
```

## 📚 참고 자료
- Spring Security Filter
- JWT 인증 패턴
```

---

### 🎯 Phase 2: Board Service 기본 구현

#### Issue #6: Board Service 프로젝트 초기 설정
**Labels**: `board-service`, `setup`, `phase-2`
**Milestone**: Phase 2 - Board Service

**Description**:
```markdown
## 📝 작업 내용
Board Service 프로젝트 기본 설정 및 데이터베이스 연결

## ✅ 체크리스트
- [ ] application.properties 설정
  - [ ] MySQL 연결 정보 (localhost:3307/boarddb)
  - [ ] JPA 설정
  - [ ] Server port: 8081
- [ ] build.gradle 의존성 확인
- [ ] Application 클래스 생성
- [ ] 서버 실행 테스트 (Port 8081)

## 🧪 테스트 방법
```bash
./gradlew :board-service:bootRun
# http://localhost:8081 접속 확인
```
```

---

#### Issue #7: Board 엔티티 및 Repository 구현
**Labels**: `board-service`, `feature`, `phase-2`
**Depends on**: #6

**Description**:
```markdown
## 📝 작업 내용
게시글 정보를 저장할 Board 엔티티 및 Repository 구현

## ✅ 체크리스트
- [ ] Board 엔티티 클래스 생성
  - [ ] id (Long, @GeneratedValue)
  - [ ] title (String, 200자)
  - [ ] content (String, TEXT)
  - [ ] userId (Long, 작성자 ID)
  - [ ] createdAt (LocalDateTime)
  - [ ] updatedAt (LocalDateTime)
- [ ] BoardRepository 인터페이스 생성
  - [ ] JpaRepository 상속
  - [ ] findByUserId(Long userId) 메서드
  - [ ] findAllByOrderByCreatedAtDesc() 메서드
- [ ] 테이블 자동 생성 확인

## 🧪 테스트 방법
```bash
docker exec -it board-mysql mysql -uroot -proot boarddb
SHOW TABLES;
DESC boards;
```
```

---

#### Issue #8: 게시글 작성 API 구현
**Labels**: `board-service`, `feature`, `api`, `phase-2`
**Depends on**: #7

**Description**:
```markdown
## 📝 작업 내용
게시글 작성 API 구현 (POST /api/boards)

## ✅ 체크리스트
- [ ] DTO 클래스 생성
  - [ ] CreateBoardRequest (title, content)
  - [ ] BoardResponse (id, title, content, userId, createdAt)
- [ ] BoardService 구현
  - [ ] 게시글 저장 로직
  - [ ] userId는 JWT에서 추출
- [ ] BoardController 구현
  - [ ] POST /api/boards 엔드포인트
  - [ ] JWT 인증 필수
  - [ ] 성공 응답: 201 Created
- [ ] JWT 인증 필터 추가
- [ ] 통합 테스트 작성

## 🧪 테스트 방법
```bash
curl -X POST http://localhost:8081/api/boards \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "첫 게시글",
    "content": "내용입니다"
  }'
```
```

---

#### Issue #9: 게시글 조회 API 구현
**Labels**: `board-service`, `feature`, `api`, `phase-2`
**Depends on**: #8

**Description**:
```markdown
## 📝 작업 내용
게시글 목록 및 단건 조회 API 구현

## ✅ 체크리스트
- [ ] BoardService 구현
  - [ ] 전체 목록 조회 (최신순)
  - [ ] 단건 조회 by ID
  - [ ] 페이징 처리 (선택)
- [ ] BoardController 구현
  - [ ] GET /api/boards - 목록 조회
  - [ ] GET /api/boards/{id} - 단건 조회
  - [ ] 인증 불필요 (Public API)
- [ ] 통합 테스트 작성

## 🧪 테스트 방법
```bash
# 목록 조회
curl http://localhost:8081/api/boards

# 단건 조회
curl http://localhost:8081/api/boards/1
```
```

---

#### Issue #10: 게시글 수정/삭제 API 구현
**Labels**: `board-service`, `feature`, `api`, `phase-2`
**Depends on**: #9

**Description**:
```markdown
## 📝 작업 내용
게시글 수정 및 삭제 API 구현 (작성자 본인만 가능)

## ✅ 체크리스트
- [ ] DTO 클래스 생성
  - [ ] UpdateBoardRequest (title, content)
- [ ] BoardService 구현
  - [ ] 작성자 권한 확인 (userId 일치 여부)
  - [ ] 수정 로직
  - [ ] 삭제 로직
- [ ] BoardController 구현
  - [ ] PUT /api/boards/{id} - 수정
  - [ ] DELETE /api/boards/{id} - 삭제
  - [ ] JWT 인증 필수
  - [ ] 권한 없으면 403 Forbidden
- [ ] 예외 처리
  - [ ] UnauthorizedAccessException
- [ ] 통합 테스트 작성

## 🧪 테스트 방법
```bash
# 수정
curl -X PUT http://localhost:8081/api/boards/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"수정된 제목","content":"수정된 내용"}'

# 삭제
curl -X DELETE http://localhost:8081/api/boards/1 \
  -H "Authorization: Bearer $TOKEN"
```
```

---

### 🎯 Phase 3: MSA 핵심 패턴

#### Issue #11: Service-to-Service 통신 구현
**Labels**: `board-service`, `feature`, `msa`, `phase-3`
**Depends on**: #10
**Milestone**: Phase 3 - MSA Patterns

**Description**:
```markdown
## 📝 작업 내용
Board Service에서 User Service를 호출하여 작성자 정보 포함

## ✅ 체크리스트
- [ ] RestTemplate 또는 WebClient 설정
- [ ] User Service 클라이언트 구현
  - [ ] GET /api/users/{id} 호출
  - [ ] 응답 DTO 매핑
- [ ] BoardResponse 확장
  - [ ] authorUsername 필드 추가
  - [ ] authorEmail 필드 추가
- [ ] 게시글 조회 시 작성자 정보 함께 반환
- [ ] 에러 처리
  - [ ] User Service 장애 시 fallback
- [ ] 통합 테스트

## 🧪 테스트 방법
```bash
# 게시글 조회 → 작성자 정보 포함 확인
curl http://localhost:8081/api/boards/1

# 기대 응답:
# {
#   "id": 1,
#   "title": "...",
#   "content": "...",
#   "userId": 1,
#   "authorUsername": "testuser",
#   "authorEmail": "test@example.com",
#   "createdAt": "..."
# }
```

## 📚 참고 자료
- MSA Service-to-Service 통신 패턴
- RestTemplate vs WebClient
```

---

#### Issue #12: 공통 모듈 확장 (Response DTO, 예외 처리)
**Labels**: `common`, `feature`, `phase-3`

**Description**:
```markdown
## 📝 작업 내용
공통 응답 포맷 및 전역 예외 처리 구현

## ✅ 체크리스트
- [ ] 공통 Response DTO
  - [ ] ApiResponse<T> (success, message, data)
  - [ ] ErrorResponse (error, message, timestamp)
- [ ] 전역 예외 처리
  - [ ] @RestControllerAdvice
  - [ ] 400, 401, 403, 404, 500 처리
  - [ ] 커스텀 예외 클래스들
- [ ] JWT 인터셉터를 common으로 이동
  - [ ] JwtAuthenticationInterceptor
  - [ ] 양쪽 서비스에서 재사용
- [ ] User/Board Service에 적용
- [ ] 테스트 작성

## 📚 참고 자료
- Spring Global Exception Handler
- Common Module 설계 패턴
```

---

### 🎯 Phase 4: 고급 MSA 패턴 (선택)

#### Issue #13: API Gateway 구현
**Labels**: `infrastructure`, `gateway`, `phase-4`, `enhancement`
**Milestone**: Phase 4 - Advanced Patterns

**Description**:
```markdown
## 📝 작업 내용
Spring Cloud Gateway를 통한 단일 진입점 구현

## ✅ 체크리스트
- [ ] gateway-service 모듈 생성
- [ ] Spring Cloud Gateway 의존성 추가
- [ ] 라우팅 설정
  - [ ] /api/users/** → user-service:8080
  - [ ] /api/boards/** → board-service:8081
- [ ] JWT 검증을 Gateway에서 처리
- [ ] CORS 설정
- [ ] Rate Limiting (선택)
- [ ] 테스트

## 🧪 테스트 방법
```bash
# Gateway를 통한 요청 (Port 8080)
curl http://localhost:8080/api/users/1
curl http://localhost:8080/api/boards/1
```

## 📚 참고 자료
- Spring Cloud Gateway 공식 문서
```

---

#### Issue #14: 서비스 디스커버리 구현
**Labels**: `infrastructure`, `eureka`, `phase-4`, `enhancement`

**Description**:
```markdown
## 📝 작업 내용
Eureka Server를 통한 서비스 디스커버리 구현

## ✅ 체크리스트
- [ ] eureka-server 모듈 생성
- [ ] Eureka Server 설정
- [ ] User/Board Service를 Eureka Client로 등록
- [ ] Gateway에서 Eureka 기반 라우팅
- [ ] 테스트

## 📚 참고 자료
- Spring Cloud Netflix Eureka
```

---

## 📊 Issue 생성 순서 요약

```
Phase 0 (완료)
└─ #0 Docker & CI/CD

Phase 1: User Service
├─ #1 User Service 설정
├─ #2 User 엔티티 & Repository
├─ #3 회원가입 API
├─ #4 로그인 & JWT
└─ #5 사용자 조회 & 인증

Phase 2: Board Service
├─ #6 Board Service 설정
├─ #7 Board 엔티티 & Repository
├─ #8 게시글 작성 API
├─ #9 게시글 조회 API
└─ #10 게시글 수정/삭제 API

Phase 3: MSA 패턴
├─ #11 Service-to-Service 통신
└─ #12 공통 모듈 확장

Phase 4: 고급 패턴 (선택)
├─ #13 API Gateway
└─ #14 Service Discovery
```

---

## 🏷️ Label 가이드

- `user-service`: User Service 관련
- `board-service`: Board Service 관련
- `common`: 공통 모듈
- `infrastructure`: 인프라 설정
- `setup`: 프로젝트 설정
- `feature`: 새 기능 구현
- `api`: API 엔드포인트
- `security`: 보안 관련
- `msa`: MSA 패턴
- `enhancement`: 선택적 개선사항
- `phase-1`, `phase-2`, `phase-3`, `phase-4`: 단계 구분

---

## 🎯 Milestone 가이드

- **Phase 1 - User Service**: Issue #1~#5
- **Phase 2 - Board Service**: Issue #6~#10
- **Phase 3 - MSA Patterns**: Issue #11~#12
- **Phase 4 - Advanced Patterns**: Issue #13~#14

---

## 📝 Issue 생성 방법

### GitHub UI 사용
1. Repository → Issues → New issue
2. Title, Labels, Milestone 설정
3. Description 복사/붙여넣기
4. Create issue

### GitHub CLI 사용
```bash
# Issue #1 생성 예시
gh issue create \
  --title "[User Service] 프로젝트 초기 설정" \
  --body-file .github/issues/issue-01.md \
  --label "user-service,setup,phase-1" \
  --milestone "Phase 1 - User Service"
```

---

## ✅ 작업 진행 방법

1. Issue 할당받기
2. 브랜치 생성: `feature/user-service/setup`
3. 개발 진행
4. 체크리스트 완료 확인
5. 테스트 실행
6. PR 생성 (Issue 연결)
7. 코드 리뷰
8. Merge 후 Issue Close

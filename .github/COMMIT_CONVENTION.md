# Git Commit Convention

이 프로젝트는 [Conventional Commits](https://www.conventionalcommits.org/) 규칙을 따릅니다.

## 📝 커밋 메시지 형식

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

## 🏷️ Type (필수)

| Type | 설명 | 예시 |
|------|------|------|
| `feat` | 새로운 기능 추가 | `feat(user-service): Add login API` |
| `fix` | 버그 수정 | `fix(board-service): Fix null pointer exception` |
| `docs` | 문서 수정 | `docs(readme): Update setup instructions` |
| `style` | 코드 포맷팅 (기능 변경 없음) | `style(user-service): Format code with Prettier` |
| `refactor` | 코드 리팩토링 | `refactor(common): Simplify JWT validation logic` |
| `test` | 테스트 추가/수정 | `test(user-service): Add login integration tests` |
| `chore` | 빌드, 설정 파일 수정 | `chore(docker): Update MySQL version to 8.0` |
| `perf` | 성능 개선 | `perf(board-service): Add database index` |

## 🎯 Scope (선택, 권장)

프로젝트의 어느 부분이 변경되었는지 명시:

- `common` - 공통 모듈
- `user-service` - 사용자 서비스
- `board-service` - 게시판 서비스
- `docker` - Docker 관련 설정
- `gradle` - Gradle 빌드 설정

## ✍️ Subject (필수)

- 현재 시제 사용: "Add" (O), "Added" (X)
- 첫 글자 대문자
- 마침표 없음
- 50자 이내

## 📖 Body (선택)

- 무엇을, 왜 변경했는지 설명
- 어떻게 변경했는지는 코드로 알 수 있음

## 🔗 Footer (선택)

- Issue 참조: `Closes #123`, `Refs #456`
- Breaking Changes: `BREAKING CHANGE: description`

## 💡 예시

### 간단한 커밋
```bash
feat(user-service): Add user registration endpoint
```

### 상세한 커밋
```bash
feat(user-service): Add user registration endpoint

- Implement User entity with validation
- Add UserRepository and UserService
- Create POST /api/users/signup endpoint
- Add password encryption with BCrypt

Closes #12
```

### Breaking Change
```bash
feat(common)!: Change JWT expiration time

BREAKING CHANGE: JWT token expiration changed from 24h to 1h.
All clients need to handle token refresh more frequently.
```

## 🌿 브랜치 네이밍

```
feature/<scope>/<description>  # 새 기능
fix/<scope>/<description>       # 버그 수정
hotfix/<description>            # 긴급 수정
docs/<description>              # 문서 작업
```

**예시**:
```bash
feature/user-service/authentication
fix/board-service/validation-error
docs/api-documentation
```

## ✅ 커밋 체크리스트

커밋 전 확인:
- [ ] 테스트가 통과하는가?
- [ ] 빌드가 성공하는가?
- [ ] 커밋 메시지가 규칙을 따르는가?
- [ ] 하나의 논리적 변경만 포함하는가?
- [ ] 민감한 정보(.env 등)가 포함되지 않았는가?

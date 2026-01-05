# Git Branch Strategy

이 프로젝트는 **GitHub Flow** 기반의 간소화된 브랜치 전략을 사용합니다.

## 🌳 브랜치 구조

```
master (main)
  ├── feature/user-service/auth
  ├── feature/board-service/crud
  ├── feature/docker-setup
  ├── fix/user-service/validation
  └── docs/api-documentation
```

## 📌 주요 브랜치

### `master` (또는 `main`)
- **목적**: 항상 배포 가능한 상태 유지
- **보호**: Direct push 금지 (PR을 통해서만 병합)
- **배포**: 이 브랜치에 병합되면 배포 가능

## 🔀 작업 브랜치

### 브랜치 네이밍 규칙

```
<type>/<scope>/<description>

예시:
feature/user-service/login
feature/board-service/create-post
fix/common/jwt-validation
docs/setup-guide
chore/docker/mysql-config
```

### Type별 용도

| Type | 용도 | 예시 |
|------|------|------|
| `feature/` | 새로운 기능 개발 | `feature/user-service/signup` |
| `fix/` | 버그 수정 | `fix/board-service/null-check` |
| `hotfix/` | 긴급 버그 수정 | `hotfix/security-vulnerability` |
| `refactor/` | 코드 리팩토링 | `refactor/common/jwt-utility` |
| `docs/` | 문서 작업 | `docs/api-specification` |
| `test/` | 테스트 추가/수정 | `test/user-service/integration` |
| `chore/` | 설정, 빌드 관련 | `chore/gradle/dependencies` |

## 🚀 워크플로우

### 1. 새 기능 개발

```bash
# 1. master 최신화
git checkout master
git pull origin master

# 2. 작업 브랜치 생성
git checkout -b feature/user-service/login

# 3. 개발 및 커밋
git add .
git commit -m "feat(user-service): Implement login logic"

# 4. 원격 저장소에 푸시
git push origin feature/user-service/login

# 5. Pull Request 생성 (GitHub)
# - Base: master
# - Compare: feature/user-service/login

# 6. 코드 리뷰 및 승인

# 7. master에 병합 (Squash & Merge 또는 Merge commit)

# 8. 로컬 브랜치 정리
git checkout master
git pull origin master
git branch -d feature/user-service/login
```

### 2. 버그 수정

```bash
# hotfix는 master에서 직접 분기
git checkout master
git checkout -b hotfix/critical-security-issue

# 수정 후 즉시 master에 병합
git checkout master
git merge hotfix/critical-security-issue
git push origin master
```

## 📋 Pull Request 규칙

### PR 제목 형식
```
[Scope] Brief description

예시:
[User Service] Implement user authentication
[Board Service] Add CRUD operations for posts
[Docker] Setup MySQL containers
```

### PR 설명 템플릿
```markdown
## 📝 변경 사항
- 주요 변경 사항 요약

## 🎯 목적
- 왜 이 변경이 필요한가?

## 🧪 테스트
- [ ] 단위 테스트 통과
- [ ] 통합 테스트 통과
- [ ] 수동 테스트 완료

## 📸 스크린샷 (필요시)

## 🔗 관련 이슈
Closes #123
```

## ⚠️ 주의사항

### ✅ DO
- 작은 단위로 자주 커밋
- 의미 있는 브랜치 이름 사용
- PR 전에 master 최신화
- 코드 리뷰 적극 활용

### ❌ DON'T
- master에 직접 push
- 여러 기능을 한 브랜치에서 개발
- 커밋 메시지 대충 작성
- .env 파일 커밋

## 🔄 브랜치 최신화

```bash
# 작업 중 master 변경사항 반영
git checkout feature/my-feature
git fetch origin
git rebase origin/master

# 또는 merge 사용
git merge origin/master
```

## 🗑️ 브랜치 정리

```bash
# 로컬 브랜치 삭제
git branch -d feature/completed-feature

# 강제 삭제
git branch -D feature/abandoned-feature

# 원격 브랜치 삭제
git push origin --delete feature/old-feature

# 병합된 브랜치 일괄 삭제
git branch --merged | grep -v "master" | xargs git branch -d
```

## 📊 브랜치 수명

| Type | 수명 | 병합 후 |
|------|------|---------|
| `feature/` | 1-3일 | 삭제 |
| `fix/` | 몇 시간 | 삭제 |
| `hotfix/` | 즉시 | 삭제 |
| `master` | 영구 | - |

## 🎓 MSA 프로젝트 특화 전략

### 모듈별 독립 개발
```bash
# 각 서비스는 독립적으로 개발
feature/user-service/authentication
feature/board-service/crud-operations

# 공통 모듈 변경 시 주의
feature/common/jwt-utility  # 모든 서비스에 영향
```

### 동시 개발 시나리오
```bash
개발자 A: feature/user-service/login
개발자 B: feature/board-service/create-post
개발자 C: feature/common/error-handler

# 각자 독립적으로 개발 후 순차적 병합
```

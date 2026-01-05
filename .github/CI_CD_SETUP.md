# CI/CD 설정 가이드

이 문서는 GitHub Actions를 사용한 CI/CD 파이프라인 설정 방법을 설명합니다.

## 📋 목차

1. [CI/CD 개요](#cicd-개요)
2. [GitHub Secrets 설정](#github-secrets-설정)
3. [워크플로우 활성화](#워크플로우-활성화)
4. [단계별 도입 전략](#단계별-도입-전략)
5. [트러블슈팅](#트러블슈팅)

---

## 🎯 CI/CD 개요

### 구축된 워크플로우

| 워크플로우 | 파일 | 목적 | 실행 시점 |
|-----------|------|------|----------|
| **Basic CI** | `ci.yml` | 전체 빌드 & 테스트 | Push/PR |
| **Multi-Module CI** | `multi-module-ci.yml` | 변경된 모듈만 빌드 | Push/PR |
| **Docker Build** | `docker-build.yml` | Docker 이미지 생성 | master Push |
| **CD Deploy** | `cd-deploy.yml` | 서버 자동 배포 | 이미지 빌드 후 |

### CI/CD 플로우

```
┌─────────────┐
│ Git Push    │
└──────┬──────┘
       │
       v
┌─────────────┐
│ CI: Build   │ ← ci.yml
│ & Test      │
└──────┬──────┘
       │ ✅ Success
       v
┌─────────────┐
│ Docker      │ ← docker-build.yml
│ Build       │
└──────┬──────┘
       │ ✅ Success
       v
┌─────────────┐
│ CD: Deploy  │ ← cd-deploy.yml
│ to Server   │
└─────────────┘
```

---

## 🔐 GitHub Secrets 설정

### 1. Secrets 추가 방법

GitHub 리포지토리에서:
```
Settings → Secrets and variables → Actions → New repository secret
```

### 2. 필수 Secrets 목록

#### Docker Hub (이미지 저장소)

| Secret 이름 | 설명 | 예시 |
|------------|------|------|
| `DOCKER_USERNAME` | Docker Hub 사용자명 | `your-dockerhub-username` |
| `DOCKER_PASSWORD` | Docker Hub 비밀번호 또는 Token | `dckr_pat_xxxxx` |

**Docker Hub Token 생성**:
1. https://hub.docker.com 로그인
2. Account Settings → Security → New Access Token
3. 생성된 토큰을 `DOCKER_PASSWORD`에 등록

#### 배포 서버 (CD 사용 시)

| Secret 이름 | 설명 | 예시 |
|------------|------|------|
| `DEV_SERVER_HOST` | 개발 서버 IP 또는 도메인 | `dev.example.com` |
| `DEV_SERVER_USER` | SSH 접속 사용자 | `ubuntu` |
| `DEV_SERVER_SSH_KEY` | SSH Private Key | `-----BEGIN RSA PRIVATE KEY-----...` |
| `PROD_SERVER_HOST` | 프로덕션 서버 IP | `prod.example.com` |
| `PROD_SERVER_USER` | SSH 접속 사용자 | `ubuntu` |
| `PROD_SERVER_SSH_KEY` | SSH Private Key | `-----BEGIN RSA PRIVATE KEY-----...` |

#### 선택사항 (알림)

| Secret 이름 | 설명 |
|------------|------|
| `SLACK_WEBHOOK` | Slack 알림용 Webhook URL |

### 3. SSH Key 생성 방법

서버 배포를 위한 SSH Key 생성:

```bash
# 1. 로컬에서 SSH Key 생성
ssh-keygen -t rsa -b 4096 -C "github-actions" -f github-actions-key

# 2. Public Key를 서버에 등록
cat github-actions-key.pub
# → 서버의 ~/.ssh/authorized_keys에 추가

# 3. Private Key를 GitHub Secrets에 등록
cat github-actions-key
# → DEV_SERVER_SSH_KEY에 전체 내용 복사
```

---

## ⚡ 워크플로우 활성화

### 단계 1: 기본 CI만 활성화 (추천)

처음 시작하시는 분은 `ci.yml`만 활성화하세요:

```bash
# 다른 워크플로우 비활성화 (파일명 변경)
mv .github/workflows/multi-module-ci.yml .github/workflows/multi-module-ci.yml.disabled
mv .github/workflows/docker-build.yml .github/workflows/docker-build.yml.disabled
mv .github/workflows/cd-deploy.yml .github/workflows/cd-deploy.yml.disabled

# Git에 커밋
git add .github/workflows/
git commit -m "chore(ci): Enable basic CI workflow"
git push origin master
```

### 단계 2: GitHub에서 확인

1. GitHub 리포지토리 → **Actions** 탭 클릭
2. 워크플로우 실행 확인
3. 빌드 로그 확인

### 단계 3: 결과 확인

✅ **성공 시**:
- 녹색 체크 마크 표시
- PR에 자동으로 상태 표시

❌ **실패 시**:
- 빨간색 X 표시
- 로그에서 오류 원인 확인
- 수정 후 다시 Push

---

## 📊 단계별 도입 전략

### Level 1: CI 기본 (현재 상태)

```yaml
# .github/workflows/ci.yml 활성화
✅ ci.yml
```

**목표**: 모든 Push마다 빌드 & 테스트 자동 실행

**확인**:
```bash
git push origin master
# GitHub Actions 탭에서 실행 확인
```

### Level 2: 멀티 모듈 CI (개발 진행 후)

```yaml
✅ ci.yml
✅ multi-module-ci.yml  # 활성화
```

**목표**: 변경된 모듈만 빌드하여 시간 절약

**활성화**:
```bash
mv .github/workflows/multi-module-ci.yml.disabled .github/workflows/multi-module-ci.yml
git add .github/workflows/multi-module-ci.yml
git commit -m "chore(ci): Enable multi-module CI"
git push
```

### Level 3: Docker 이미지 빌드 (배포 준비)

```yaml
✅ ci.yml
✅ multi-module-ci.yml
✅ docker-build.yml  # 활성화
```

**전제조건**:
- Docker Hub 계정 생성
- `DOCKER_USERNAME`, `DOCKER_PASSWORD` Secrets 설정

**활성화**:
```bash
# Docker Hub Secrets 설정 완료 확인 후
mv .github/workflows/docker-build.yml.disabled .github/workflows/docker-build.yml
git add .github/workflows/docker-build.yml
git commit -m "chore(ci): Enable Docker image build"
git push
```

### Level 4: 자동 배포 (프로덕션 준비)

```yaml
✅ ci.yml
✅ multi-module-ci.yml
✅ docker-build.yml
✅ cd-deploy.yml  # 활성화
```

**전제조건**:
- 배포 서버 준비 (AWS EC2, DigitalOcean 등)
- SSH Key 설정
- `DEV_SERVER_*` Secrets 설정

**활성화**:
```bash
# 모든 Secrets 설정 완료 확인 후
mv .github/workflows/cd-deploy.yml.disabled .github/workflows/cd-deploy.yml
git add .github/workflows/cd-deploy.yml
git commit -m "chore(cd): Enable auto deployment"
git push
```

---

## 🚀 빠른 시작 (초보자용)

### 지금 바로 시작하기

```bash
# 1. 기본 CI만 활성화 (다른 워크플로우 비활성화)
cd .github/workflows/
for file in multi-module-ci.yml docker-build.yml cd-deploy.yml; do
  mv "$file" "${file}.disabled"
done

# 2. Git에 커밋
git add .
git commit -m "chore(ci): Setup basic CI workflow only"
git push origin master

# 3. GitHub Actions 탭에서 확인
# https://github.com/YOUR_USERNAME/YOUR_REPO/actions
```

### 다음 단계

1. **코드 작성**: User Service 개발
2. **Push**: `git push origin master`
3. **확인**: GitHub Actions에서 자동 빌드 확인
4. **점진적 확장**: 필요에 따라 Level 2, 3, 4 활성화

---

## 🔧 트러블슈팅

### 문제 1: `Permission denied` 오류

```bash
# gradlew 실행 권한 확인
chmod +x gradlew
git add gradlew
git commit -m "chore: Add execute permission to gradlew"
git push
```

### 문제 2: Docker Hub 로그인 실패

**원인**: `DOCKER_PASSWORD`가 비밀번호가 아닌 Token이어야 함

**해결**:
1. Docker Hub → Account Settings → Security
2. New Access Token 생성
3. 생성된 토큰을 `DOCKER_PASSWORD`에 등록

### 문제 3: 빌드 시간이 너무 오래 걸림

**해결**:
- Gradle 의존성 캐싱 활성화 (이미 적용됨)
- `multi-module-ci.yml` 사용 (변경된 모듈만 빌드)

### 문제 4: 테스트가 CI에서만 실패

**원인**: 환경 차이 (로컬 vs CI)

**해결**:
```bash
# 로컬에서 CI와 동일하게 테스트
docker run --rm -v $(pwd):/app -w /app eclipse-temurin:17-jdk ./gradlew test
```

---

## 📚 더 알아보기

### GitHub Actions 문서
- https://docs.github.com/en/actions

### Docker Hub
- https://hub.docker.com

### Deployment 옵션
- **AWS EC2**: https://aws.amazon.com/ec2/
- **DigitalOcean**: https://www.digitalocean.com/
- **Heroku**: https://www.heroku.com/
- **Railway**: https://railway.app/

---

## 🎯 권장 사항

### 개인 학습 프로젝트 (현재)
```
✅ Level 1: 기본 CI (ci.yml)
⏭️ Level 2: 나중에 필요 시
⏭️ Level 3: Docker 학습 후
⏭️ Level 4: 서버 준비 후
```

### 포트폴리오용
```
✅ Level 1: 기본 CI
✅ Level 2: 멀티 모듈 CI
✅ Level 3: Docker 빌드
⏭️ Level 4: 무료 서버로 배포 (Railway, Heroku)
```

### 실무 수준
```
✅ Level 1~4 모두 활성화
✅ 코드 커버리지 측정
✅ 보안 스캔
✅ 블루-그린 배포
```
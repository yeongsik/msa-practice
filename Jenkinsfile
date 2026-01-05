// ===================================
// Jenkins Pipeline for MSA Project
// ===================================
// Declarative Pipeline 문법 사용

pipeline {
    // Agent 설정 (어디서 실행할지)
    agent any

    // 환경변수 정의
    environment {
        JAVA_HOME = tool 'JDK17'
        GRADLE_HOME = tool 'Gradle8'
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_CREDENTIALS_ID = 'docker-hub-credentials'
        SLACK_CHANNEL = '#ci-cd-alerts'
    }

    // 빌드 매개변수
    parameters {
        choice(
            name: 'SERVICE',
            choices: ['all', 'user-service', 'board-service'],
            description: '빌드할 서비스 선택'
        )
        booleanParam(
            name: 'DEPLOY',
            defaultValue: false,
            description: '배포 실행 여부'
        )
    }

    // 트리거 설정
    triggers {
        // GitHub webhook 연동
        githubPush()

        // 주기적 실행 (매일 밤 12시)
        cron('0 0 * * *')
    }

    // 빌드 단계
    stages {

        // Stage 1: 환경 준비
        stage('Preparation') {
            steps {
                echo '=== Starting CI/CD Pipeline ==='

                // Git 정보 출력
                sh '''
                    echo "Branch: ${GIT_BRANCH}"
                    echo "Commit: ${GIT_COMMIT}"
                '''

                // 작업 공간 정리
                cleanWs()

                // 코드 체크아웃
                checkout scm
            }
        }

        // Stage 2: 빌드
        stage('Build') {
            steps {
                script {
                    if (params.SERVICE == 'all' || params.SERVICE == 'user-service') {
                        echo '=== Building User Service ==='
                        sh './gradlew :user-service:clean :user-service:build --no-daemon'
                    }

                    if (params.SERVICE == 'all' || params.SERVICE == 'board-service') {
                        echo '=== Building Board Service ==='
                        sh './gradlew :board-service:clean :board-service:build --no-daemon'
                    }
                }
            }
        }

        // Stage 3: 테스트
        stage('Test') {
            parallel {
                stage('User Service Tests') {
                    when {
                        expression {
                            params.SERVICE == 'all' || params.SERVICE == 'user-service'
                        }
                    }
                    steps {
                        sh './gradlew :user-service:test --no-daemon'
                    }
                }

                stage('Board Service Tests') {
                    when {
                        expression {
                            params.SERVICE == 'all' || params.SERVICE == 'board-service'
                        }
                    }
                    steps {
                        sh './gradlew :board-service:test --no-daemon'
                    }
                }
            }

            // 테스트 결과 수집
            post {
                always {
                    junit '**/build/test-results/**/*.xml'

                    // JaCoCo 코드 커버리지 리포트
                    jacoco(
                        execPattern: '**/build/jacoco/*.exec',
                        classPattern: '**/build/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        // Stage 4: 코드 품질 분석
        stage('Code Quality') {
            parallel {
                stage('Checkstyle') {
                    steps {
                        sh './gradlew checkstyleMain checkstyleTest --no-daemon'
                    }
                }

                stage('SpotBugs') {
                    steps {
                        sh './gradlew spotbugsMain --no-daemon'
                    }
                }

                stage('SonarQube') {
                    when {
                        branch 'master'
                    }
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh './gradlew sonarqube --no-daemon'
                        }
                    }
                }
            }
        }

        // Stage 5: Docker 이미지 빌드
        stage('Docker Build') {
            when {
                branch 'master'
            }
            steps {
                script {
                    // User Service 이미지
                    def userImage = docker.build(
                        "your-dockerhub/user-service:${env.BUILD_NUMBER}",
                        "-f user-service/Dockerfile ."
                    )

                    // Board Service 이미지
                    def boardImage = docker.build(
                        "your-dockerhub/board-service:${env.BUILD_NUMBER}",
                        "-f board-service/Dockerfile ."
                    )

                    // 이미지를 환경변수에 저장 (다음 스테이지에서 사용)
                    env.USER_IMAGE = userImage.id
                    env.BOARD_IMAGE = boardImage.id
                }
            }
        }

        // Stage 6: Docker Hub에 푸시
        stage('Docker Push') {
            when {
                branch 'master'
            }
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', DOCKER_CREDENTIALS_ID) {
                        docker.image(env.USER_IMAGE).push()
                        docker.image(env.USER_IMAGE).push('latest')

                        docker.image(env.BOARD_IMAGE).push()
                        docker.image(env.BOARD_IMAGE).push('latest')
                    }
                }
            }
        }

        // Stage 7: 배포 (선택적)
        stage('Deploy') {
            when {
                expression { params.DEPLOY == true }
                branch 'master'
            }
            steps {
                script {
                    // 개발 서버에 배포
                    sshagent(['dev-server-ssh-key']) {
                        sh '''
                            ssh user@dev-server.com << EOF
                                cd /app/msa-practice
                                docker-compose pull
                                docker-compose up -d
                                docker-compose ps
                            EOF
                        '''
                    }
                }
            }
        }

        // Stage 8: 스모크 테스트
        stage('Smoke Test') {
            when {
                expression { params.DEPLOY == true }
            }
            steps {
                script {
                    // 배포된 서비스 헬스체크
                    sh '''
                        sleep 10
                        curl -f http://dev-server.com:8080/actuator/health || exit 1
                        curl -f http://dev-server.com:8081/actuator/health || exit 1
                    '''
                }
            }
        }
    }

    // 빌드 후 작업
    post {
        always {
            echo '=== Pipeline Finished ==='

            // 빌드 결과 아카이빙
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true

            // 워크스페이스 정리
            cleanWs()
        }

        success {
            echo '=== Build Successful ==='

            // Slack 알림
            slackSend(
                channel: SLACK_CHANNEL,
                color: 'good',
                message: "✅ Build #${env.BUILD_NUMBER} succeeded - ${env.JOB_NAME}"
            )
        }

        failure {
            echo '=== Build Failed ==='

            // Slack 알림
            slackSend(
                channel: SLACK_CHANNEL,
                color: 'danger',
                message: "❌ Build #${env.BUILD_NUMBER} failed - ${env.JOB_NAME}\nCheck: ${env.BUILD_URL}"
            )

            // 이메일 알림
            emailext(
                subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed. Check ${env.BUILD_URL}",
                to: 'team@example.com'
            )
        }

        unstable {
            echo '=== Build Unstable ==='
            slackSend(
                channel: SLACK_CHANNEL,
                color: 'warning',
                message: "⚠️ Build #${env.BUILD_NUMBER} unstable - ${env.JOB_NAME}"
            )
        }
    }
}
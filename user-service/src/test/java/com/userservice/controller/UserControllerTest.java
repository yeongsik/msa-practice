package com.userservice.controller;

import com.userservice.config.TestConfig;
import com.userservice.dto.SignUpRequest;
import com.userservice.dto.UserResponse;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserController 통합 테스트
 * WebTestClient 사용 (Spring Boot 4.0.0 권장 방식)
 *
 * 참고: 상세한 비즈니스 로직 테스트는 UserServiceTest에서 수행
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
)
@Import(TestConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 DB 초기화
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users/signup - 회원가입 성공 (201)")
    void signUp_Success() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();

        // when & then
        webTestClient.post()
                .uri("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getUsername()).isEqualTo("testuser");
                    assertThat(response.getEmail()).isEqualTo("test@example.com");
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getCreatedAt()).isNotNull();
                });
    }

    @Test
    @DisplayName("POST /api/users/signup - 중복 사용자명 (409)")
    void signUp_Fail_DuplicateUsername() {
        // given - 첫 번째 사용자 생성
        SignUpRequest firstRequest = SignUpRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test1@example.com")
                .build();

        webTestClient.post()
                .uri("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(firstRequest)
                .exchange()
                .expectStatus().isCreated();

        // given - 중복 사용자명으로 요청
        SignUpRequest duplicateRequest = SignUpRequest.builder()
                .username("testuser")
                .password("password456")
                .email("test2@example.com")
                .build();

        // when & then
        webTestClient.post()
                .uri("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(duplicateRequest)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    @DisplayName("POST /api/users/signup - 유효성 검증 실패 (400)")
    void signUp_Fail_ValidationError() {
        // given - 짧은 사용자명
        SignUpRequest request = SignUpRequest.builder()
                .username("ab")  // 3자 미만
                .password("password123")
                .email("test@example.com")
                .build();

        // when & then
        webTestClient.post()
                .uri("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}

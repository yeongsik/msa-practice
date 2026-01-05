package com.userservice.controller;

import com.userservice.dto.SignUpRequest;
import com.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * UserController 통합 테스트
 * MockMvc 사용
 *
 * 참고: 상세한 비즈니스 로직 테스트는 UserServiceTest에서 수행
 */
@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // MockMvc 수동 설정 (Security 필터 제외)
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        // 각 테스트 전에 DB 초기화
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/users/signup - 회원가입 성공 (201)")
    void signUp_Success() throws Exception {
        // given
        String requestBody = """
                {
                    "username": "testuser",
                    "password": "password123",
                    "email": "test@example.com"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())  // 응답 출력
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/users/signup - 중복 사용자명 (409)")
    void signUp_Fail_DuplicateUsername() throws Exception {
        // given - 첫 번째 사용자 생성
        String firstRequest = """
                {
                    "username": "testuser",
                    "password": "password123",
                    "email": "test1@example.com"
                }
                """;

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequest))
                .andExpect(status().isCreated());

        // given - 중복 사용자명으로 요청
        String duplicateRequest = """
                {
                    "username": "testuser",
                    "password": "password456",
                    "email": "test2@example.com"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateRequest))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /api/users/signup - 유효성 검증 실패 (400)")
    void signUp_Fail_ValidationError() throws Exception {
        // given - 짧은 사용자명
        String request = """
                {
                    "username": "ab",
                    "password": "password123",
                    "email": "test@example.com"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }
}

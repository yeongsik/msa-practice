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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Collections;

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

    @Test
    @DisplayName("POST /api/users/login - 로그인 성공 (200)")
    void login_Success() throws Exception {
        // given - 먼저 회원가입
        String signupRequest = """
                {
                    "username": "loginuser",
                    "password": "password123",
                    "email": "login@example.com"
                }
                """;

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isCreated());

        // given - 로그인 요청
        String loginRequest = """
                {
                    "username": "loginuser",
                    "password": "password123"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/users/login - 로그인 실패: 존재하지 않는 사용자 (401)")
    void login_Fail_UserNotFound() throws Exception {
        // given
        String loginRequest = """
                {
                    "username": "nonexistent",
                    "password": "password123"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/users/login - 로그인 실패: 비밀번호 불일치 (401)")
    void login_Fail_WrongPassword() throws Exception {
        // given - 먼저 회원가입
        String signupRequest = """
                {
                    "username": "testuser2",
                    "password": "password123",
                    "email": "test2@example.com"
                }
                """;

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isCreated());

        // given - 틀린 비밀번호로 로그인
        String loginRequest = """
                {
                    "username": "testuser2",
                    "password": "wrongpassword"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/users/login - 유효성 검증 실패 (400)")
    void login_Fail_ValidationError() throws Exception {
        // given - 빈 사용자명
        String loginRequest = """
                {
                    "username": "",
                    "password": ""
                }
                """;

        // when & then
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users/me - 내 정보 조회 성공 (200)")
    void getMyInfo_Success() throws Exception {
        // given - 회원가입
        String signupRequest = """
                {
                    "username": "meuser",
                    "password": "password123",
                    "email": "me@example.com"
                }
                """;

        String response = mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 응답에서 userId 추출 (간단하게 JSON 파싱)
        Long userId = extractUserId(response);

        // given - 인증 정보 생성
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/users/me")
                        .with(authentication(auth)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("meuser"))
                .andExpect(jsonPath("$.data.email").value("me@example.com"))
                .andExpect(jsonPath("$.data.id").value(userId));
    }

    @Test
    @DisplayName("GET /api/users/me - 인증 없음 (401)")
    void getMyInfo_Fail_Unauthorized() throws Exception {
        // when & then - 인증 정보 없이 요청
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - 정보 수정 성공 (200)")
    void updateUser_Success() throws Exception {
        // given - 회원가입
        String signupRequest = """
                {
                    "username": "updateuser",
                    "password": "password123",
                    "email": "update@example.com"
                }
                """;

        String response = mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = extractUserId(response);

        // given - 인증 정보 및 수정 요청
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        String updateRequest = """
                {
                    "currentPassword": "password123",
                    "newEmail": "newemail@example.com"
                }
                """;

        // when & then
        mockMvc.perform(put("/api/users/" + userId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.data.username").value("updateuser"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - 권한 없음: 다른 사용자 정보 수정 시도 (403)")
    void updateUser_Fail_Forbidden() throws Exception {
        // given - 두 명의 사용자 생성
        String signupRequest1 = """
                {
                    "username": "user1",
                    "password": "password123",
                    "email": "user1@example.com"
                }
                """;

        String signupRequest2 = """
                {
                    "username": "user2",
                    "password": "password123",
                    "email": "user2@example.com"
                }
                """;

        String response1 = mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest1))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String response2 = mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest2))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId1 = extractUserId(response1);
        Long userId2 = extractUserId(response2);

        // given - user1로 인증했지만 user2의 정보를 수정하려고 시도
        Authentication auth = new UsernamePasswordAuthenticationToken(userId1, null, Collections.emptyList());
        String updateRequest = """
                {
                    "currentPassword": "password123",
                    "newEmail": "hacker@example.com"
                }
                """;

        // when & then
        mockMvc.perform(put("/api/users/" + userId2)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - 유효성 검증 실패: 현재 비밀번호 누락 (400)")
    void updateUser_Fail_ValidationError() throws Exception {
        // given - 회원가입
        String signupRequest = """
                {
                    "username": "validuser",
                    "password": "password123",
                    "email": "valid@example.com"
                }
                """;

        String response = mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupRequest))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = extractUserId(response);

        // given - 인증 정보, 현재 비밀번호 없는 요청
        Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
        String updateRequest = """
                {
                    "newEmail": "newemail@example.com"
                }
                """;

        // when & then
        mockMvc.perform(put("/api/users/" + userId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isBadRequest());
    }

    /**
     * JSON 응답에서 userId 추출 헬퍼 메서드
     */
    private Long extractUserId(String jsonResponse) {
        try {
            // 간단한 JSON 파싱: "\"id\":1" 패턴 찾기
            String idPattern = "\"id\":";
            int idStart = jsonResponse.indexOf(idPattern) + idPattern.length();
            int idEnd = jsonResponse.indexOf(",", idStart);
            if (idEnd == -1) {
                idEnd = jsonResponse.indexOf("}", idStart);
            }
            String idStr = jsonResponse.substring(idStart, idEnd).trim();
            return Long.parseLong(idStr);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract userId from response: " + jsonResponse, e);
        }
    }
}

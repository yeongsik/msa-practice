package org.userservice.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.userservice.application.dto.LoginRequest;
import org.userservice.application.dto.LoginResponse;
import org.userservice.application.usecase.AuthService;
import org.userservice.application.usecase.UserService;
import org.userservice.common.exception.BusinessException;
import org.userservice.common.exception.ErrorCode;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 로그인 컨트롤러 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LoginControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private UserService userService;
    
    @MockitoBean
    private AuthService authService;
    
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    
    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@example.com", "password123");
        loginResponse = new LoginResponse(
                1L,
                "testuser",
                "test@example.com",
                "access_token",
                "refresh_token",
                LocalDateTime.now().plusMinutes(15)
        );
    }
    
    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // Given
        given(authService.login(any(LoginRequest.class)))
                .willReturn(loginResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh_token"))
                .andExpect(jsonPath("$.data.tokenExpiry").exists());
    }
    
    @Test
    @DisplayName("잘못된 로그인 정보로 로그인 시도 시 400 에러")
    void login_WrongCredentials_Returns400() throws Exception {
        // Given
        given(authService.login(any(LoginRequest.class)))
                .willThrow(new BusinessException(ErrorCode.USER_007, "잘못된 로그인 정보입니다."));
        
        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("USER-007"))
                .andExpect(jsonPath("$.message").value("잘못된 로그인 정보입니다."));
    }
    
    @Test
    @DisplayName("필수 필드 누락 시 400 에러")
    void login_MissingRequiredFields_Returns400() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest("", "");
        
        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
    
    @Test
    @DisplayName("잘못된 JSON 형식 시 400 에러")
    void login_InvalidJsonFormat_Returns400() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Content-Type이 application/json이 아닌 경우 415 에러")
    void login_WrongContentType_Returns415() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnsupportedMediaType());
    }
}
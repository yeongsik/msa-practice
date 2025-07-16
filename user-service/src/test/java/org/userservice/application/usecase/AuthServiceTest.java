package org.userservice.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.userservice.application.dto.LoginRequest;
import org.userservice.application.dto.LoginResponse;
import org.userservice.common.exception.BusinessException;
import org.userservice.common.exception.ErrorCode;
import org.userservice.domain.User;
import org.userservice.domain.UserRepository;
import org.userservice.infrastructure.security.JwtTokenProvider;
import org.userservice.infrastructure.security.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * AuthService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @InjectMocks
    private AuthService authService;
    
    private User testUser;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .build();
        
        loginRequest = new LoginRequest("test@example.com", "password123");
    }
    
    @Test
    @DisplayName("이메일로 로그인 성공")
    void login_WithEmail_Success() {
        // Given
        given(userRepository.findByEmail(loginRequest.loginId()))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(loginRequest.password(), testUser.getPassword()))
                .willReturn(true);
        given(jwtTokenProvider.createAccessToken(any(), anyString()))
                .willReturn("access_token");
        given(jwtTokenProvider.createRefreshToken(any()))
                .willReturn("refresh_token");
        given(jwtTokenProvider.calculateAccessTokenExpiry())
                .willReturn(LocalDateTime.now().plusMinutes(15));
        
        // When
        LoginResponse response = authService.login(loginRequest);
        
        // Then
        assertThat(response.userId()).isEqualTo(testUser.getId());
        assertThat(response.username()).isEqualTo(testUser.getUsername());
        assertThat(response.email()).isEqualTo(testUser.getEmail());
        assertThat(response.accessToken()).isEqualTo("access_token");
        assertThat(response.refreshToken()).isEqualTo("refresh_token");
        assertThat(response.tokenExpiry()).isNotNull();
        
        verify(userRepository).findByEmail(loginRequest.loginId());
        verify(passwordEncoder).matches(loginRequest.password(), testUser.getPassword());
        verify(jwtTokenProvider).createAccessToken(testUser.getId(), testUser.getUsername());
        verify(jwtTokenProvider).createRefreshToken(testUser.getId());
    }
    
    @Test
    @DisplayName("사용자명으로 로그인 성공")
    void login_WithUsername_Success() {
        // Given
        LoginRequest usernameLoginRequest = new LoginRequest("testuser", "password123");
        given(userRepository.findByEmail(usernameLoginRequest.loginId()))
                .willReturn(Optional.empty());
        given(userRepository.findByUsername(usernameLoginRequest.loginId()))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(usernameLoginRequest.password(), testUser.getPassword()))
                .willReturn(true);
        given(jwtTokenProvider.createAccessToken(any(), anyString()))
                .willReturn("access_token");
        given(jwtTokenProvider.createRefreshToken(any()))
                .willReturn("refresh_token");
        given(jwtTokenProvider.calculateAccessTokenExpiry())
                .willReturn(LocalDateTime.now().plusMinutes(15));
        
        // When
        LoginResponse response = authService.login(usernameLoginRequest);
        
        // Then
        assertThat(response.userId()).isEqualTo(testUser.getId());
        assertThat(response.username()).isEqualTo(testUser.getUsername());
        
        verify(userRepository).findByEmail(usernameLoginRequest.loginId());
        verify(userRepository).findByUsername(usernameLoginRequest.loginId());
    }
    
    @Test
    @DisplayName("존재하지 않는 사용자로 로그인 시도 시 예외 발생")
    void login_UserNotFound_ThrowsException() {
        // Given
        given(userRepository.findByEmail(loginRequest.loginId()))
                .willReturn(Optional.empty());
        given(userRepository.findByUsername(loginRequest.loginId()))
                .willReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("잘못된 로그인 정보입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_007);
    }
    
    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도 시 예외 발생")
    void login_WrongPassword_ThrowsException() {
        // Given
        given(userRepository.findByEmail(loginRequest.loginId()))
                .willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(loginRequest.password(), testUser.getPassword()))
                .willReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("잘못된 로그인 정보입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_007);
    }
    
    @Test
    @DisplayName("토큰 검증 및 사용자 정보 조회 성공")
    void validateTokenAndGetUser_Success() {
        // Given
        String validToken = "valid_token";
        given(jwtTokenProvider.validateToken(validToken))
                .willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken(validToken))
                .willReturn(testUser.getId());
        given(userRepository.findByIdAndNotDeleted(testUser.getId()))
                .willReturn(Optional.of(testUser));
        
        // When
        User result = authService.validateTokenAndGetUser(validToken);
        
        // Then
        assertThat(result).isEqualTo(testUser);
        verify(jwtTokenProvider).validateToken(validToken);
        verify(jwtTokenProvider).getUserIdFromToken(validToken);
        verify(userRepository).findByIdAndNotDeleted(testUser.getId());
    }
    
    @Test
    @DisplayName("잘못된 토큰으로 사용자 정보 조회 시 예외 발생")
    void validateTokenAndGetUser_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid_token";
        given(jwtTokenProvider.validateToken(invalidToken))
                .willReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> authService.validateTokenAndGetUser(invalidToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("유효하지 않은 토큰입니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }
    
    @Test
    @DisplayName("토큰은 유효하지만 사용자가 존재하지 않는 경우 예외 발생")
    void validateTokenAndGetUser_UserNotFound_ThrowsException() {
        // Given
        String validToken = "valid_token";
        given(jwtTokenProvider.validateToken(validToken))
                .willReturn(true);
        given(jwtTokenProvider.getUserIdFromToken(validToken))
                .willReturn(999L);
        given(userRepository.findByIdAndNotDeleted(999L))
                .willReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> authService.validateTokenAndGetUser(validToken))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_003);
    }
}
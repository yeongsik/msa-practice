package com.userservice.service;

import java.util.Optional;

import com.common.util.JwtUtil;
import com.userservice.dto.LoginRequest;
import com.userservice.dto.LoginResponse;
import com.userservice.dto.MyInfoResponse;
import com.userservice.dto.SignUpRequest;
import com.userservice.dto.UpdateUserRequest;
import com.userservice.dto.UpdateUserResponse;
import com.userservice.dto.UserResponse;
import com.userservice.entity.RefreshToken;
import com.userservice.entity.User;
import com.userservice.exception.DuplicateEmailException;
import com.userservice.exception.DuplicateUsernameException;
import com.userservice.exception.InvalidCredentialsException;
import com.userservice.repository.RefreshTokenRepository;
import com.userservice.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * UserService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private UserService userService;

    private MockedStatic<JwtUtil> jwtUtilMock;

    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        signUpRequest = SignUpRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .build();
    }

    @AfterEach
    void tearDown() {
        if (jwtUtilMock != null) {
            jwtUtilMock.close();
        }
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponse response = userService.signUp(signUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 사용자명")
    void signUp_Fail_DuplicateUsername() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequest))
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessageContaining("testuser");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void signUp_Fail_DuplicateEmail() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(signUpRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("test@example.com");

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        jwtUtilMock = mockStatic(JwtUtil.class);
        jwtUtilMock.when(() -> JwtUtil.generateAccessToken(anyLong())).thenReturn("access-token");
        jwtUtilMock.when(() -> JwtUtil.generateRefreshToken(anyLong())).thenReturn("refresh-token");

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(null);

        // when
        LoginResponse response = userService.login(loginRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(refreshTokenRepository).deleteByUserId(1L);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Fail_UserNotFound() {
        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_PasswordMismatch() {
        // given
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    @DisplayName("비밀번호 암호화 확인")
    void signUp_PasswordEncoded() {
        // given
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        userService.signUp(signUpRequest);

        // then
        verify(passwordEncoder).encode("password123");
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_Success() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        MyInfoResponse response = userService.getMyInfo(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 사용자 없음")
    void getMyInfo_Fail_UserNotFound() {
        // given
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMyInfo(999L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 이메일만 변경")
    void updateUser_Success_EmailOnly() {
        // given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .currentPassword("password123")
                .newEmail("newemail@example.com")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(userRepository.existsByEmail("newemail@example.com")).willReturn(false);

        // when
        UpdateUserResponse response = userService.updateUser(1L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("newemail@example.com");

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(userRepository).existsByEmail("newemail@example.com");
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 비밀번호만 변경")
    void updateUser_Success_PasswordOnly() {
        // given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .currentPassword("password123")
                .newPassword("newPassword456")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(passwordEncoder.encode("newPassword456")).willReturn("newEncodedPassword");

        // when
        UpdateUserResponse response = userService.updateUser(1L, request);

        // then
        assertThat(response).isNotNull();

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(passwordEncoder).encode("newPassword456");
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 이메일과 비밀번호 모두 변경")
    void updateUser_Success_EmailAndPassword() {
        // given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .currentPassword("password123")
                .newEmail("newemail@example.com")
                .newPassword("newPassword456")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(userRepository.existsByEmail("newemail@example.com")).willReturn(false);
        given(passwordEncoder.encode("newPassword456")).willReturn("newEncodedPassword");

        // when
        UpdateUserResponse response = userService.updateUser(1L, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("newemail@example.com");

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(passwordEncoder).encode("newPassword456");
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 현재 비밀번호 불일치")
    void updateUser_Fail_InvalidCurrentPassword() {
        // given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .currentPassword("wrongPassword")
                .newEmail("newemail@example.com")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("현재 비밀번호가 일치하지 않습니다");

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
        verify(userRepository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 이메일 중복")
    void updateUser_Fail_DuplicateEmail() {
        // given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .currentPassword("password123")
                .newEmail("duplicate@example.com")
                .build();

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(userRepository.existsByEmail("duplicate@example.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(userRepository).existsByEmail("duplicate@example.com");
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 사용자 없음")
    void updateUser_Fail_UserNotFound() {
        // given
        UpdateUserRequest request = UpdateUserRequest.builder()
                .currentPassword("password123")
                .newEmail("newemail@example.com")
                .build();

        given(userRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUser(999L, request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다");

        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).existsByEmail(anyString());
    }
}
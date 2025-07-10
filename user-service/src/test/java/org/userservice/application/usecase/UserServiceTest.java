package org.userservice.application.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.userservice.application.dto.UserCreateRequest;
import org.userservice.application.dto.UserResponse;
import org.userservice.common.exception.BusinessException;
import org.userservice.common.exception.ErrorCode;
import org.userservice.domain.User;
import org.userservice.domain.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 - 정상적인 요청으로 사용자가 생성된다")
    void createUser_ValidRequest_ReturnsUserResponse() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
                "john_doe",
                "john@example.com",
                "password123!",
                "Hello, I'm John!",
                null
        );

        User savedUser = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("encrypted_password")
                .bio("Hello, I'm John!")
                .profileImage("https://default-profile-image.com/default.png")
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123!")).thenReturn("encrypted_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse result = userService.createUser(request);

        // Then
        assertThat(result.username()).isEqualTo("john_doe");
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.bio()).isEqualTo("Hello, I'm John!");
        assertThat(result.profileImage()).isEqualTo("https://default-profile-image.com/default.png");

        verify(userRepository).existsByUsername("john_doe");
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 사용자명으로 예외가 발생한다")
    void createUser_DuplicateUsername_ThrowsBusinessException() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
                "john_doe",
                "john@example.com",
                "password123!",
                "Hello, I'm John!",
                null
        );

        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_001);

        verify(userRepository).existsByUsername("john_doe");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 - 중복된 이메일로 예외가 발생한다")
    void createUser_DuplicateEmail_ThrowsBusinessException() {
        // Given
        UserCreateRequest request = new UserCreateRequest(
                "john_doe",
                "john@example.com",
                "password123!",
                "Hello, I'm John!",
                null
        );

        when(userRepository.existsByUsername("john_doe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_002);

        verify(userRepository).existsByUsername("john_doe");
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 조회 - ID로 사용자를 찾는다")
    void findById_ExistingUser_ReturnsUserResponse() {
        // Given
        Long userId = 1L;
        User user = User.builder()
                .username("john_doe")
                .email("john@example.com")
                .password("encrypted_password")
                .bio("Hello, I'm John!")
                .profileImage("profile.jpg")
                .build();

        when(userRepository.findByIdAndNotDeleted(userId)).thenReturn(Optional.of(user));

        // When
        UserResponse result = userService.findById(userId);

        // Then
        assertThat(result.username()).isEqualTo("john_doe");
        assertThat(result.email()).isEqualTo("john@example.com");
        assertThat(result.bio()).isEqualTo("Hello, I'm John!");

        verify(userRepository).findByIdAndNotDeleted(userId);
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 ID로 예외가 발생한다")
    void findById_NonExistingUser_ThrowsBusinessException() {
        // Given
        Long userId = 999L;

        when(userRepository.findByIdAndNotDeleted(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findById(userId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_003);

        verify(userRepository).findByIdAndNotDeleted(userId);
    }

    @Test
    @DisplayName("사용자명 존재 확인 - 존재하는 사용자명인 경우 true를 반환한다")
    void existsByUsername_ExistingUsername_ReturnsTrue() {
        // Given
        String username = "john_doe";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        // When
        boolean result = userService.existsByUsername(username);

        // Then
        assertThat(result).isTrue();

        verify(userRepository).existsByUsername(username);
    }

    @Test
    @DisplayName("이메일 존재 확인 - 존재하지 않는 이메일인 경우 false를 반환한다")
    void existsByEmail_NonExistingEmail_ReturnsFalse() {
        // Given
        String email = "new@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        // When
        boolean result = userService.existsByEmail(email);

        // Then
        assertThat(result).isFalse();

        verify(userRepository).existsByEmail(email);
    }
}
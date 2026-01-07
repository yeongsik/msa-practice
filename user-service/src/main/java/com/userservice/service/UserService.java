package com.userservice.service;

import com.common.util.JwtUtil;
import com.userservice.dto.LoginRequest;
import com.userservice.dto.LoginResponse;
import com.userservice.dto.MyInfoResponse;
import com.userservice.dto.SignUpRequest;
import com.userservice.dto.UpdateUserRequest;
import com.userservice.dto.UpdateUserResponse;
import com.userservice.dto.UserResponse;
import com.userservice.entity.User;
import com.userservice.exception.DuplicateEmailException;
import com.userservice.exception.DuplicateUsernameException;
import com.userservice.exception.InvalidCredentialsException;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 사용자 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 정보 수정
     *
     * @param userId  사용자 ID
     * @param request 수정 요청 정보
     * @return UpdateUserResponse
     */
    @Transactional
    public UpdateUserResponse updateUser(Long userId, UpdateUserRequest request) {
        log.info("사용자 정보 수정 시도: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 1. 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 이메일 변경 시 중복 체크
        if (StringUtils.hasText(request.getNewEmail()) && !request.getNewEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getNewEmail())) {
                throw new DuplicateEmailException(request.getNewEmail());
            }
            user.updateEmail(request.getNewEmail());
        }

        // 3. 비밀번호 변경
        if (StringUtils.hasText(request.getNewPassword())) {
            user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return UpdateUserResponse.from(user);
    }

    /**
     * 내 정보 조회
     *
     * @param userId 사용자 ID
     * @return MyInfoResponse
     */
    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return MyInfoResponse.from(user);
    }

    /**
     * 사용자 정보 조회 (공통)
     *
     * @param userId 사용자 ID
     * @return UserResponse
     */
    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    /**
     * 로그인
     *
     * @param request 로그인 요청
     * @return LoginResponse (JWT 토큰)
     * @throws InvalidCredentialsException 사용자 정보가 일치하지 않을 경우
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("로그인 시도: username={}", request.getUsername());

        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    log.warn("로그인 실패: 존재하지 않는 사용자 (username={})", request.getUsername());
                    return new InvalidCredentialsException("사용자명 또는 비밀번호가 올바르지 않습니다.");
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("로그인 실패: 비밀번호 불일치 (username={})", request.getUsername());
            throw new InvalidCredentialsException("사용자명 또는 비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 생성
        String token = JwtUtil.generateToken(user.getId());
        log.info("로그인 성공: username={}, userId={}", user.getUsername(), user.getId());

        return LoginResponse.builder()
                .accessToken(token)
                .build();
    }

    /**
     * 토큰 검증 및 사용자 정보 반환
     *
     * @param token JWT 토큰 (Bearer 제외)
     * @return UserDto
     */
    @Transactional(readOnly = true)
    public com.common.dto.UserDto validateToken(String token) {
        if (!JwtUtil.validate(token)) {
            throw new InvalidCredentialsException("유효하지 않은 토큰입니다.");
        }

        Long userId = JwtUtil.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return com.common.dto.UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role("USER") // 추후 Role 필드 추가 시 변경
                .build();
    }

    /**
     * 회원가입
     *
     * @param request 회원가입 요청
     * @return UserResponse
     * @throws DuplicateUsernameException 사용자명 중복
     * @throws DuplicateEmailException 이메일 중복
     */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        log.info("회원가입 시도: username={}, email={}", request.getUsername(), request.getEmail());

        // 사용자명 중복 체크
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("사용자명 중복: {}", request.getUsername());
            throw new DuplicateUsernameException(request.getUsername());
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("이메일 중복: {}", request.getEmail());
            throw new DuplicateEmailException(request.getEmail());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .email(request.getEmail())
                .build();

        // 저장
        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: id={}, username={}", savedUser.getId(), savedUser.getUsername());

        return UserResponse.from(savedUser);
    }
}
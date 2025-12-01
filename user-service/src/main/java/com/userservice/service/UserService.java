package com.userservice.service;

import com.userservice.dto.SignUpRequest;
import com.userservice.dto.UserResponse;
import com.userservice.entity.User;
import com.userservice.exception.DuplicateEmailException;
import com.userservice.exception.DuplicateUsernameException;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
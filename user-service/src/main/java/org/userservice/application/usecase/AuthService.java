package org.userservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.application.dto.LoginRequest;
import org.userservice.application.dto.LoginResponse;
import org.userservice.common.exception.BusinessException;
import org.userservice.common.exception.ErrorCode;
import org.userservice.domain.User;
import org.userservice.domain.UserRepository;
import org.userservice.infrastructure.security.JwtTokenProvider;
import org.userservice.infrastructure.security.PasswordEncoder;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    /**
     * 사용자 로그인 처리
     * 
     * @param loginRequest 로그인 요청 정보
     * @return 로그인 응답 정보 (토큰 포함)
     */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 사용자 조회 (이메일 또는 사용자명으로)
        User user = findUserByLoginId(loginRequest.loginId());
        
        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            log.warn("로그인 실패 - 잘못된 비밀번호: loginId={}", loginRequest.loginId());
            throw new BusinessException(ErrorCode.USER_007, "잘못된 로그인 정보입니다.");
        }
        
        // 3. 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        
        log.info("사용자 로그인 성공: userId={}, username={}", user.getId(), user.getUsername());
        
        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                accessToken,
                refreshToken,
                jwtTokenProvider.calculateAccessTokenExpiry()
        );
    }
    
    /**
     * 로그인 ID로 사용자 조회 (이메일 또는 사용자명)
     */
    private User findUserByLoginId(String loginId) {
        User user = null;
        
        // 이메일 형식인지 확인
        if (loginId.contains("@")) {
            user = userRepository.findByEmail(loginId)
                    .orElse(null);
        }
        
        // 이메일로 찾지 못했거나 이메일 형식이 아닌 경우 사용자명으로 조회
        if (user == null) {
            user = userRepository.findByUsername(loginId)
                    .orElse(null);
        }
        
        // 사용자를 찾지 못한 경우
        if (user == null) {
            log.warn("로그인 실패 - 사용자 없음: loginId={}", loginId);
            throw new BusinessException(ErrorCode.USER_007, "잘못된 로그인 정보입니다.");
        }
        
        return user;
    }
    
    /**
     * 토큰 검증 및 사용자 정보 조회
     */
    public User validateTokenAndGetUser(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_003, "사용자를 찾을 수 없습니다."));
    }
}
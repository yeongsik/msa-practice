package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * JWT Access Token
     */
    private String accessToken;

    /**
     * 토큰 타입 (Bearer)
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * 만료 시간 (ms)
     * JwtUtil의 EXPIRATION 값과 일치해야 함
     */
    @Builder.Default
    private long expiresIn = 86400000;
}

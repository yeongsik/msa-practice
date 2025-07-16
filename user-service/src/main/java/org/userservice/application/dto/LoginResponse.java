package org.userservice.application.dto;

import java.time.LocalDateTime;

/**
 * 로그인 응답 DTO
 */
public record LoginResponse(
        Long userId,
        String username,
        String email,
        String accessToken,
        String refreshToken,
        LocalDateTime tokenExpiry
) {
}
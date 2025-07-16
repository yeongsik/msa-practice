package org.userservice.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO
 */
public record LoginRequest(
        @NotBlank(message = "이메일 또는 사용자명을 입력해주세요.")
        String loginId,  // 이메일 또는 사용자명
        
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
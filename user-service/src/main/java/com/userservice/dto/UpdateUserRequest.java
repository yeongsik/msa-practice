package com.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 정보 수정 요청 DTO
 * 비밀번호 변경과 이메일 변경을 선택적으로 수행
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    /**
     * 현재 비밀번호 (본인 확인용, 필수)
     */
    @Size(min = 1, message = "현재 비밀번호를 입력해주세요")
    private String currentPassword;

    /**
     * 새로운 비밀번호 (선택)
     */
    @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다")
    private String newPassword;

    /**
     * 변경할 이메일 (선택)
     */
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String newEmail;
}

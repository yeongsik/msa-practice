package com.userservice.dto;

import com.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 수정 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserResponse {

    private Long id;
    private String username;
    private String email;
    private LocalDateTime updatedAt;

    public static UpdateUserResponse from(User user) {
        return UpdateUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

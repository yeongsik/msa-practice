package com.userservice.dto;

import com.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 내 정보 조회 응답 DTO
 * (UserResponse와 거의 같지만, 추후 민감한 정보 포함 여부가 달라질 수 있어 분리)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoResponse {

    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MyInfoResponse from(User user) {
        return MyInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .thumbnailUrl(user.getThumbnailUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}

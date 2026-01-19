package com.userservice.dto;

import com.userservice.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 응답 DTO
 * 비밀번호 제외한 사용자 정보 반환
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 이메일
     */
    private String email;

    /**
     * 프로필 이미지 URL
     */
    private String profileImageUrl;

    /**
     * 썸네일 이미지 URL
     */
    private String thumbnailUrl;

    /**
     * 생성일시
     */
    private LocalDateTime createdAt;

    /**
     * User 엔티티로부터 UserResponse 생성
     *
     * @param user User 엔티티
     * @return UserResponse
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .thumbnailUrl(user.getThumbnailUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
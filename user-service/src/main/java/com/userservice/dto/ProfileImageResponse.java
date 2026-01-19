package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 프로필 이미지 업로드/수정 응답 DTO.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileImageResponse {

    private Long userId;
    private String profileImageUrl;
    private String thumbnailUrl;
    private LocalDateTime uploadedAt;

    public static ProfileImageResponse of(Long userId, String profileImageUrl, String thumbnailUrl) {
        return ProfileImageResponse.builder()
                .userId(userId)
                .profileImageUrl(profileImageUrl)
                .thumbnailUrl(thumbnailUrl)
                .uploadedAt(LocalDateTime.now())
                .build();
    }
}

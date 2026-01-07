package com.boardservice.dto.interaction;

import com.boardservice.entity.PostLike;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 좋아요 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;

    /**
     * 엔티티로부터 DTO 생성.
     */
    public static LikeResponse from(PostLike like) {
        return LikeResponse.builder()
                .id(like.getId())
                .postId(like.getPost().getId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}

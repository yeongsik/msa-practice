package com.boardservice.dto.interaction;

import com.boardservice.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글 통계 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostStatsResponse {

    private Long postId;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;
    private Integer shareCount;
    private Boolean isLiked;        // 현재 사용자의 좋아요 여부
    private Boolean isBookmarked;   // 현재 사용자의 북마크 여부

    /**
     * 엔티티로부터 DTO 생성.
     */
    public static PostStatsResponse from(Post post, Boolean isLiked, Boolean isBookmarked) {
        return PostStatsResponse.builder()
                .postId(post.getId())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .shareCount(post.getShareCount())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .build();
    }

    /**
     * 비로그인 사용자용.
     */
    public static PostStatsResponse from(Post post) {
        return from(post, false, false);
    }
}

package com.boardservice.dto.post;

import com.boardservice.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 상세 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;
    private String title;
    private String content;  // 전체 내용
    private Long userId;
    private String username;
    private Long boardId;
    private String boardName;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;
    private Integer shareCount;
    private Boolean isLiked;        // 현재 사용자의 좋아요 여부
    private Boolean isBookmarked;   // 현재 사용자의 북마크 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 엔티티로부터 DTO 생성 (전체 content 포함).
     */
    public static PostDetailResponse from(Post post, String username, Boolean isLiked, Boolean isBookmarked) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())  // 전체 내용
                .userId(post.getUserId())
                .username(username)
                .boardId(post.getBoard().getId())
                .boardName(post.getBoard().getName())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .shareCount(post.getShareCount())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * 비로그인 사용자용 (isLiked, isBookmarked = false).
     */
    public static PostDetailResponse from(Post post, String username) {
        return from(post, username, false, false);
    }
}

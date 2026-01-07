package com.boardservice.dto.post;

import com.boardservice.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글 응답 DTO (목록용).
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String content;  // 요약본 (200자)
    private Long userId;
    private String username;
    private Long boardId;
    private String boardName;
    private Long viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer bookmarkCount;
    private Integer shareCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 엔티티로부터 DTO 생성 (content 요약).
     */
    public static PostResponse from(Post post, String username) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(truncate(post.getContent(), 200))
                .userId(post.getUserId())
                .username(username)
                .boardId(post.getBoard().getId())
                .boardName(post.getBoard().getName())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .bookmarkCount(post.getBookmarkCount())
                .shareCount(post.getShareCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /**
     * 문자열을 지정한 길이로 자르기.
     */
    private static String truncate(String content, int maxLength) {
        if (content == null) {
            return null;
        }
        if (content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }
}

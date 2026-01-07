package com.boardservice.dto.interaction;

import com.boardservice.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 북마크 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;

    /**
     * 엔티티로부터 DTO 생성.
     */
    public static BookmarkResponse from(Bookmark bookmark) {
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .postId(bookmark.getPost().getId())
                .userId(bookmark.getUserId())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}

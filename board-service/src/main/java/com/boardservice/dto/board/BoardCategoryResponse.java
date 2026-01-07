package com.boardservice.dto.board;

import com.boardservice.entity.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시판 카테고리 응답 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Integer postCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 엔티티로부터 DTO 생성.
     */
    public static BoardCategoryResponse from(BoardCategory category) {
        return BoardCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .postCount(category.getPostCount())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

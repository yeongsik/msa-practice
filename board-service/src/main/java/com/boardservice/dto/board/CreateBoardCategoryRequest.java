package com.boardservice.dto.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시판 카테고리 생성 요청 DTO.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBoardCategoryRequest {

    @NotBlank(message = "게시판 이름은 필수입니다")
    @Size(max = 100, message = "게시판 이름은 100자를 초과할 수 없습니다")
    private String name;

    @Size(max = 500, message = "게시판 설명은 500자를 초과할 수 없습니다")
    private String description;
}

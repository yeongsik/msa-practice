package com.boardservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.boardservice.dto.board.BoardCategoryResponse;
import com.boardservice.dto.board.CreateBoardCategoryRequest;
import com.boardservice.entity.BoardCategory;
import com.boardservice.repository.BoardCategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * BoardCategoryService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class BoardCategoryServiceTest {

    @Mock
    private BoardCategoryRepository boardCategoryRepository;

    @InjectMocks
    private BoardCategoryService boardCategoryService;

    private BoardCategory boardCategory;
    private CreateBoardCategoryRequest createRequest;

    @BeforeEach
    void setUp() {
        boardCategory = BoardCategory.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .isActive(true)
                .postCount(0)
                .build();

        createRequest = CreateBoardCategoryRequest.builder()
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .build();
    }

    @Test
    @DisplayName("게시판 생성 성공")
    void createBoardCategory_Success() {
        // given
        given(boardCategoryRepository.existsByName("자유게시판")).willReturn(false);
        given(boardCategoryRepository.save(any(BoardCategory.class))).willReturn(boardCategory);

        // when
        BoardCategoryResponse response = boardCategoryService.createBoardCategory(createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("자유게시판");
        assertThat(response.getDescription()).isEqualTo("자유롭게 소통하는 공간");

        verify(boardCategoryRepository).existsByName("자유게시판");
        verify(boardCategoryRepository).save(any(BoardCategory.class));
    }

    @Test
    @DisplayName("게시판 생성 실패 - 중복된 이름")
    void createBoardCategory_Fail_DuplicateName() {
        // given
        given(boardCategoryRepository.existsByName("자유게시판")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> boardCategoryService.createBoardCategory(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름입니다");

        verify(boardCategoryRepository).existsByName("자유게시판");
    }

    @Test
    @DisplayName("전체 게시판 목록 조회")
    void getAllBoardCategories_Success() {
        // given
        BoardCategory category2 = BoardCategory.builder()
                .id(2L)
                .name("공지사항")
                .description("중요 공지")
                .isActive(true)
                .postCount(0)
                .build();

        given(boardCategoryRepository.findAll()).willReturn(Arrays.asList(boardCategory, category2));

        // when
        List<BoardCategoryResponse> responses = boardCategoryService.getAllBoardCategories();

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("자유게시판");
        assertThat(responses.get(1).getName()).isEqualTo("공지사항");

        verify(boardCategoryRepository).findAll();
    }

    @Test
    @DisplayName("활성화된 게시판 목록 조회")
    void getActiveBoardCategories_Success() {
        // given
        given(boardCategoryRepository.findByIsActiveTrueOrderByCreatedAtAsc())
                .willReturn(Arrays.asList(boardCategory));

        // when
        List<BoardCategoryResponse> responses = boardCategoryService.getActiveBoardCategories();

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("자유게시판");
        assertThat(responses.get(0).getIsActive()).isTrue();

        verify(boardCategoryRepository).findByIsActiveTrueOrderByCreatedAtAsc();
    }

    @Test
    @DisplayName("게시판 단건 조회 성공")
    void getBoardCategory_Success() {
        // given
        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));

        // when
        BoardCategoryResponse response = boardCategoryService.getBoardCategory(1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("자유게시판");

        verify(boardCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("게시판 단건 조회 실패 - 존재하지 않는 게시판")
    void getBoardCategory_Fail_NotFound() {
        // given
        given(boardCategoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardCategoryService.getBoardCategory(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판을 찾을 수 없습니다");

        verify(boardCategoryRepository).findById(999L);
    }

    @Test
    @DisplayName("게시판 수정 성공")
    void updateBoardCategory_Success() {
        // given
        CreateBoardCategoryRequest updateRequest = CreateBoardCategoryRequest.builder()
                .name("수정된 게시판")
                .description("수정된 설명")
                .build();

        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));
        given(boardCategoryRepository.existsByName("수정된 게시판")).willReturn(false);

        // when
        BoardCategoryResponse response = boardCategoryService.updateBoardCategory(1L, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("수정된 게시판");
        assertThat(response.getDescription()).isEqualTo("수정된 설명");

        verify(boardCategoryRepository).findById(1L);
        verify(boardCategoryRepository).existsByName("수정된 게시판");
    }

    @Test
    @DisplayName("게시판 수정 실패 - 중복된 이름")
    void updateBoardCategory_Fail_DuplicateName() {
        // given
        CreateBoardCategoryRequest updateRequest = CreateBoardCategoryRequest.builder()
                .name("공지사항")
                .description("수정된 설명")
                .build();

        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));
        given(boardCategoryRepository.existsByName("공지사항")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> boardCategoryService.updateBoardCategory(1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 게시판 이름입니다");

        verify(boardCategoryRepository).findById(1L);
        verify(boardCategoryRepository).existsByName("공지사항");
    }

    @Test
    @DisplayName("게시판 수정 실패 - 존재하지 않는 게시판")
    void updateBoardCategory_Fail_NotFound() {
        // given
        given(boardCategoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardCategoryService.updateBoardCategory(999L, createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판을 찾을 수 없습니다");

        verify(boardCategoryRepository).findById(999L);
    }

    @Test
    @DisplayName("게시판 삭제 성공")
    void deleteBoardCategory_Success() {
        // given
        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));

        // when
        boardCategoryService.deleteBoardCategory(1L);

        // then
        verify(boardCategoryRepository).findById(1L);
        verify(boardCategoryRepository).delete(boardCategory);
    }

    @Test
    @DisplayName("게시판 삭제 실패 - 게시글이 존재함")
    void deleteBoardCategory_Fail_HasPosts() {
        // given
        boardCategory.incrementPostCount();
        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));

        // when & then
        assertThatThrownBy(() -> boardCategoryService.deleteBoardCategory(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글이 존재하는 게시판은 삭제할 수 없습니다");

        verify(boardCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("게시판 삭제 실패 - 존재하지 않는 게시판")
    void deleteBoardCategory_Fail_NotFound() {
        // given
        given(boardCategoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardCategoryService.deleteBoardCategory(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판을 찾을 수 없습니다");

        verify(boardCategoryRepository).findById(999L);
    }

    @Test
    @DisplayName("게시판 비활성화 성공")
    void deactivateBoardCategory_Success() {
        // given
        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));

        // when
        boardCategoryService.deactivateBoardCategory(1L);

        // then
        assertThat(boardCategory.getIsActive()).isFalse();

        verify(boardCategoryRepository).findById(1L);
    }

    @Test
    @DisplayName("게시판 활성화 성공")
    void activateBoardCategory_Success() {
        // given
        boardCategory.deactivate();
        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));

        // when
        boardCategoryService.activateBoardCategory(1L);

        // then
        assertThat(boardCategory.getIsActive()).isTrue();

        verify(boardCategoryRepository).findById(1L);
    }
}

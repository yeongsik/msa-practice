package com.boardservice.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.boardservice.dto.board.BoardCategoryResponse;
import com.boardservice.dto.board.CreateBoardCategoryRequest;
import com.boardservice.exception.GlobalExceptionHandler;
import com.boardservice.service.BoardCategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BoardCategoryController 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class BoardCategoryControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BoardCategoryService boardCategoryService;

    @InjectMocks
    private BoardCategoryController boardCategoryController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardCategoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/board-categories - 게시판 생성 성공")
    void createBoardCategory_Success() throws Exception {
        // given
        CreateBoardCategoryRequest request = CreateBoardCategoryRequest.builder()
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .build();

        BoardCategoryResponse response = BoardCategoryResponse.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .isActive(true)
                .postCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(boardCategoryService.createBoardCategory(any(CreateBoardCategoryRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/board-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("자유게시판"))
                .andExpect(jsonPath("$.data.description").value("자유롭게 소통하는 공간"))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    @Test
    @DisplayName("GET /api/board-categories - 전체 게시판 목록 조회")
    void getAllBoardCategories_Success() throws Exception {
        // given
        List<BoardCategoryResponse> responses = Arrays.asList(
                BoardCategoryResponse.builder()
                        .id(1L)
                        .name("자유게시판")
                        .description("자유롭게 소통")
                        .isActive(true)
                        .postCount(10)
                        .build(),
                BoardCategoryResponse.builder()
                        .id(2L)
                        .name("공지사항")
                        .description("중요 공지")
                        .isActive(true)
                        .postCount(5)
                        .build()
        );

        given(boardCategoryService.getAllBoardCategories()).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/board-categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("자유게시판"))
                .andExpect(jsonPath("$.data[1].name").value("공지사항"));
    }

    @Test
    @DisplayName("GET /api/board-categories/active - 활성화된 게시판 목록 조회")
    void getActiveBoardCategories_Success() throws Exception {
        // given
        List<BoardCategoryResponse> responses = Arrays.asList(
                BoardCategoryResponse.builder()
                        .id(1L)
                        .name("자유게시판")
                        .isActive(true)
                        .build()
        );

        given(boardCategoryService.getActiveBoardCategories()).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/board-categories/active"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].isActive").value(true));
    }

    @Test
    @DisplayName("GET /api/board-categories/{id} - 게시판 단건 조회")
    void getBoardCategory_Success() throws Exception {
        // given
        BoardCategoryResponse response = BoardCategoryResponse.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .isActive(true)
                .postCount(10)
                .build();

        given(boardCategoryService.getBoardCategory(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/board-categories/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("자유게시판"));
    }

    @Test
    @DisplayName("PUT /api/board-categories/{id} - 게시판 수정 성공")
    void updateBoardCategory_Success() throws Exception {
        // given
        CreateBoardCategoryRequest request = CreateBoardCategoryRequest.builder()
                .name("수정된 게시판")
                .description("수정된 설명")
                .build();

        BoardCategoryResponse response = BoardCategoryResponse.builder()
                .id(1L)
                .name("수정된 게시판")
                .description("수정된 설명")
                .isActive(true)
                .postCount(10)
                .build();

        given(boardCategoryService.updateBoardCategory(eq(1L), any(CreateBoardCategoryRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/board-categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("수정된 게시판"))
                .andExpect(jsonPath("$.data.description").value("수정된 설명"));
    }

    @Test
    @DisplayName("DELETE /api/board-categories/{id} - 게시판 삭제 성공")
    void deleteBoardCategory_Success() throws Exception {
        // given
        doNothing().when(boardCategoryService).deleteBoardCategory(1L);

        // when & then
        mockMvc.perform(delete("/api/board-categories/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시판이 삭제되었습니다."));
    }

    @Test
    @DisplayName("DELETE /api/board-categories/{id} - 게시글 존재 시 삭제 실패")
    void deleteBoardCategory_Fail_HasPosts() throws Exception {
        // given
        doThrow(new IllegalArgumentException("게시글이 존재하는 게시판은 삭제할 수 없습니다. postCount=10"))
                .when(boardCategoryService).deleteBoardCategory(1L);

        // when & then
        mockMvc.perform(delete("/api/board-categories/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/board-categories/{id}/deactivate - 게시판 비활성화")
    void deactivateBoardCategory_Success() throws Exception {
        // given
        doNothing().when(boardCategoryService).deactivateBoardCategory(1L);

        // when & then
        mockMvc.perform(patch("/api/board-categories/1/deactivate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시판이 비활성화되었습니다."));
    }

    @Test
    @DisplayName("PATCH /api/board-categories/{id}/activate - 게시판 활성화")
    void activateBoardCategory_Success() throws Exception {
        // given
        doNothing().when(boardCategoryService).activateBoardCategory(1L);

        // when & then
        mockMvc.perform(patch("/api/board-categories/1/activate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시판이 활성화되었습니다."));
    }

    @Test
    @DisplayName("POST /api/board-categories - 검증 실패 (이름 누락)")
    void createBoardCategory_Fail_ValidationError() throws Exception {
        // given
        CreateBoardCategoryRequest request = CreateBoardCategoryRequest.builder()
                .description("설명만 있음")
                .build();

        // when & then
        mockMvc.perform(post("/api/board-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

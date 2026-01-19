package com.boardservice.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import com.boardservice.dto.BoardResponse;
import com.boardservice.dto.CreateBoardRequest;
import com.boardservice.dto.UpdateBoardRequest;
import com.boardservice.exception.GlobalExceptionHandler;
import com.boardservice.service.BoardService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * BoardController 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class BoardControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BoardService boardService;

    @InjectMocks
    private BoardController boardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(boardController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/boards - 게시글 생성 성공")
    void createBoard_Success() throws Exception {
        // given
        CreateBoardRequest request = CreateBoardRequest.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(1L)
                .username("testuser")
                .createdAt(LocalDateTime.now())
                .build();

        given(boardService.createBoard(any(), any(CreateBoardRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.data.content").value("테스트 내용"));
    }

    @Test
    @DisplayName("POST /api/boards - 검증 실패 (제목 누락)")
    void createBoard_Fail_ValidationError_TitleMissing() throws Exception {
        // given
        CreateBoardRequest request = CreateBoardRequest.builder()
                .content("내용만 있음")
                .build();

        // when & then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/boards - 검증 실패 (내용 누락)")
    void createBoard_Fail_ValidationError_ContentMissing() throws Exception {
        // given
        CreateBoardRequest request = CreateBoardRequest.builder()
                .title("제목만 있음")
                .build();

        // when & then
        mockMvc.perform(post("/api/boards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/boards - 전체 게시글 목록 조회 성공")
    void getAllBoards_Success() throws Exception {
        // given
        BoardResponse response1 = BoardResponse.builder()
                .id(1L)
                .title("첫 번째 게시글")
                .content("첫 번째 내용")
                .userId(1L)
                .username("user1")
                .createdAt(LocalDateTime.now())
                .build();

        BoardResponse response2 = BoardResponse.builder()
                .id(2L)
                .title("두 번째 게시글")
                .content("두 번째 내용")
                .userId(2L)
                .username("user2")
                .createdAt(LocalDateTime.now())
                .build();

        given(boardService.getAllBoards()).willReturn(Arrays.asList(response1, response2));

        // when & then
        mockMvc.perform(get("/api/boards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("첫 번째 게시글"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("두 번째 게시글"));
    }

    @Test
    @DisplayName("GET /api/boards - 빈 목록 조회")
    void getAllBoards_Success_EmptyList() throws Exception {
        // given
        given(boardService.getAllBoards()).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/boards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("GET /api/boards/{id} - 게시글 단건 조회 성공")
    void getBoard_Success() throws Exception {
        // given
        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(1L)
                .username("testuser")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(boardService.getBoard(1L)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/boards/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.data.content").value("테스트 내용"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /api/boards/{id} - 게시글 조회 실패 (존재하지 않음)")
    void getBoard_Fail_NotFound() throws Exception {
        // given
        given(boardService.getBoard(999L))
                .willThrow(new IllegalArgumentException("게시글을 찾을 수 없습니다. id=999"));

        // when & then
        mockMvc.perform(get("/api/boards/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/boards/{id} - 게시글 수정 성공")
    void updateBoard_Success() throws Exception {
        // given
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        BoardResponse response = BoardResponse.builder()
                .id(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .userId(1L)
                .username("testuser")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(boardService.updateBoard(any(), eq(1L), any(UpdateBoardRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.content").value("수정된 내용"));
    }

    @Test
    @DisplayName("PUT /api/boards/{id} - 게시글 수정 실패 (권한 없음)")
    void updateBoard_Fail_Unauthorized() throws Exception {
        // given
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        given(boardService.updateBoard(any(), eq(1L), any(UpdateBoardRequest.class)))
                .willThrow(new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다."));

        // when & then
        mockMvc.perform(put("/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/boards/{id} - 게시글 수정 실패 (존재하지 않음)")
    void updateBoard_Fail_NotFound() throws Exception {
        // given
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        given(boardService.updateBoard(any(), eq(999L), any(UpdateBoardRequest.class)))
                .willThrow(new IllegalArgumentException("게시글을 찾을 수 없습니다. id=999"));

        // when & then
        mockMvc.perform(put("/api/boards/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/boards/{id} - 검증 실패 (제목 누락)")
    void updateBoard_Fail_ValidationError() throws Exception {
        // given
        UpdateBoardRequest request = UpdateBoardRequest.builder()
                .content("내용만 있음")
                .build();

        // when & then
        mockMvc.perform(put("/api/boards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/boards/{id} - 게시글 삭제 성공")
    void deleteBoard_Success() throws Exception {
        // given
        doNothing().when(boardService).deleteBoard(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/boards/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 삭제되었습니다."));
    }

    @Test
    @DisplayName("DELETE /api/boards/{id} - 게시글 삭제 실패 (권한 없음)")
    void deleteBoard_Fail_Unauthorized() throws Exception {
        // given
        doThrow(new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다."))
                .when(boardService).deleteBoard(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/boards/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/boards/{id} - 게시글 삭제 실패 (존재하지 않음)")
    void deleteBoard_Fail_NotFound() throws Exception {
        // given
        doThrow(new IllegalArgumentException("게시글을 찾을 수 없습니다. id=999"))
                .when(boardService).deleteBoard(any(), eq(999L));

        // when & then
        mockMvc.perform(delete("/api/boards/999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
package com.boardservice.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import com.boardservice.dto.comment.CommentResponse;
import com.boardservice.dto.comment.CreateCommentRequest;
import com.boardservice.dto.comment.UpdateCommentRequest;
import com.boardservice.exception.CommentNotFoundException;
import com.boardservice.exception.GlobalExceptionHandler;
import com.boardservice.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * CommentController 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/comments - 댓글 작성 성공")
    void createComment_Success() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("테스트 댓글입니다.")
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .content("테스트 댓글입니다.")
                .userId(1L)
                .username("testuser")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        given(commentService.createComment(any(), eq(1L), any(CreateCommentRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("테스트 댓글입니다."));
    }

    @Test
    @DisplayName("GET /api/posts/{postId}/comments - 게시글의 댓글 목록 조회")
    void getComments_Success() throws Exception {
        // given
        List<CommentResponse> responses = Arrays.asList(
                CommentResponse.builder()
                        .id(1L)
                        .content("첫 번째 댓글")
                        .userId(1L)
                        .username("user1")
                        .postId(1L)
                        .build(),
                CommentResponse.builder()
                        .id(2L)
                        .content("두 번째 댓글")
                        .userId(2L)
                        .username("user2")
                        .postId(1L)
                        .build()
        );

        given(commentService.getComments(1L)).willReturn(responses);

        // when & then
        mockMvc.perform(get("/api/posts/1/comments"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].content").value("첫 번째 댓글"));
    }

    @Test
    @DisplayName("GET /api/comments/users/me - 내가 작성한 댓글 목록 조회")
    void getMyComments_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .content("내 댓글")
                .userId(1L)
                .username("testuser")
                .postId(1L)
                .build();

        given(commentService.getCommentsByUser(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/comments/users/me")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].content").value("내 댓글"));
    }

    @Test
    @DisplayName("PUT /api/comments/{id} - 댓글 수정 성공")
    void updateComment_Success() throws Exception {
        // given
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("수정된 댓글")
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(1L)
                .content("수정된 댓글")
                .userId(1L)
                .username("testuser")
                .postId(1L)
                .build();

        given(commentService.updateComment(any(), eq(1L), any(UpdateCommentRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("수정된 댓글"));
    }

    @Test
    @DisplayName("PUT /api/comments/{id} - 수정 실패 (권한 없음)")
    void updateComment_Fail_Unauthorized() throws Exception {
        // given
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("수정된 댓글")
                .build();

        given(commentService.updateComment(any(), eq(1L), any(UpdateCommentRequest.class)))
                .willThrow(new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다."));

        // when & then
        mockMvc.perform(put("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} - 댓글 삭제 성공")
    void deleteComment_Success() throws Exception {
        // given
        doNothing().when(commentService).deleteComment(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/comments/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글이 삭제되었습니다."));
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} - 삭제 실패 (존재하지 않는 댓글)")
    void deleteComment_Fail_NotFound() throws Exception {
        // given
        doThrow(new CommentNotFoundException("댓글을 찾을 수 없습니다. id=999"))
                .when(commentService).deleteComment(any(), eq(999L));

        // when & then
        mockMvc.perform(delete("/api/comments/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/comments/{id} - 삭제 실패 (권한 없음)")
    void deleteComment_Fail_Unauthorized() throws Exception {
        // given
        doThrow(new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다."))
                .when(commentService).deleteComment(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/comments/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/comments - 검증 실패 (내용 누락)")
    void createComment_Fail_ValidationError() throws Exception {
        // given
        CreateCommentRequest request = CreateCommentRequest.builder().build();

        // when & then
        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
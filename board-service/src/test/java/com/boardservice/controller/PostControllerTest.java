package com.boardservice.controller;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.boardservice.dto.post.CreatePostRequest;
import com.boardservice.dto.post.PostDetailResponse;
import com.boardservice.dto.post.PostResponse;
import com.boardservice.dto.post.UpdatePostRequest;
import com.boardservice.exception.GlobalExceptionHandler;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.service.PostService;
import com.boardservice.service.ViewCountService;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
 * PostController 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private PostService postService;

    @Mock
    private ViewCountService viewCountService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/posts - 게시글 생성 성공")
    void createPost_Success() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .boardId(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .build();

        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(1L)
                .username("testuser")
                .boardId(1L)
                .boardName("자유게시판")
                .viewCount(0L)
                .likeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .build();

        given(postService.createPost(any(), any(CreatePostRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("테스트 게시글"));
    }

    @Test
    @DisplayName("GET /api/posts - 전체 게시글 목록 조회")
    void getAllPosts_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(1L)
                .username("testuser")
                .boardId(1L)
                .boardName("자유게시판")
                .build();

        given(postService.getAllPosts(any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].title").value("테스트 게시글"));
    }

    @Test
    @DisplayName("GET /api/posts/{id} - 게시글 상세 조회")
    void getPost_Success() throws Exception {
        // given
        PostDetailResponse response = PostDetailResponse.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("전체 내용입니다.")
                .userId(1L)
                .username("testuser")
                .boardId(1L)
                .boardName("자유게시판")
                .viewCount(10L)
                .likeCount(5)
                .commentCount(3)
                .isLiked(false)
                .isBookmarked(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(postService.getPost(eq(1L), any())).willReturn(response);
        doNothing().when(viewCountService).incrementViewCount(anyLong(), any());

        // when & then
        mockMvc.perform(get("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("테스트 게시글"))
                .andExpect(jsonPath("$.data.content").value("전체 내용입니다."))
                .andExpect(jsonPath("$.data.viewCount").value(10));
    }

    @Test
    @DisplayName("GET /api/posts/{id} - 게시글 조회 실패 (존재하지 않음)")
    void getPost_Fail_NotFound() throws Exception {
        // given
        given(postService.getPost(eq(999L), any()))
                .willThrow(new PostNotFoundException("게시글을 찾을 수 없습니다. id=999"));

        // when & then
        mockMvc.perform(get("/api/posts/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/posts/board-categories/{boardId} - 게시판별 게시글 조회")
    void getPostsByBoard_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("테스트 게시글")
                .boardId(1L)
                .boardName("자유게시판")
                .build();

        given(postService.getPostsByBoard(eq(1L), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts/board-categories/1")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].boardId").value(1));
    }

    @Test
    @DisplayName("GET /api/posts/users/me - 내 게시글 조회")
    void getMyPosts_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("내 게시글")
                .userId(1L)
                .build();

        given(postService.getPostsByUser(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts/users/me")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].userId").value(1));
    }

    @Test
    @DisplayName("PUT /api/posts/{id} - 게시글 수정 성공")
    void updatePost_Success() throws Exception {
        // given
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("수정된 제목")
                .content("수정된 내용")
                .userId(1L)
                .build();

        given(postService.updatePost(any(), eq(1L), any(UpdatePostRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정된 제목"));
    }

    @Test
    @DisplayName("PUT /api/posts/{id} - 수정 실패 (권한 없음)")
    void updatePost_Fail_Unauthorized() throws Exception {
        // given
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        given(postService.updatePost(any(), eq(1L), any(UpdatePostRequest.class)))
                .willThrow(new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다."));

        // when & then
        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} - 게시글 삭제 성공")
    void deletePost_Success() throws Exception {
        // given
        doNothing().when(postService).deletePost(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 삭제되었습니다."));
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} - 삭제 실패 (권한 없음)")
    void deletePost_Fail_Unauthorized() throws Exception {
        // given
        doThrow(new IllegalArgumentException("게시글 작성자만 삭제할 수 있습니다."))
                .when(postService).deletePost(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/posts/1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/posts/popular/likes - 인기 게시글 조회 (좋아요 기준)")
    void getPopularPostsByLikes_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("인기 게시글")
                .likeCount(100)
                .build();

        given(postService.getPopularPostsByLikes(any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts/popular/likes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].likeCount").value(100));
    }

    @Test
    @DisplayName("GET /api/posts/search - 게시글 검색")
    void searchPosts_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        PostResponse response = PostResponse.builder()
                .id(1L)
                .title("검색어 포함 게시글")
                .build();

        given(postService.searchPosts(eq("검색어"), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts/search")
                        .param("keyword", "검색어"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("검색어 포함 게시글"));
    }

    @Test
    @DisplayName("POST /api/posts - 검증 실패 (제목 누락)")
    void createPost_Fail_ValidationError() throws Exception {
        // given
        CreatePostRequest request = CreatePostRequest.builder()
                .boardId(1L)
                .content("내용만 있음")
                .build();

        // when & then
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
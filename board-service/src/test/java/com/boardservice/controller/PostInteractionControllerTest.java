package com.boardservice.controller;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.boardservice.dto.interaction.BookmarkResponse;
import com.boardservice.dto.interaction.LikeResponse;
import com.boardservice.dto.interaction.PostStatsResponse;
import com.boardservice.entity.ShareType;
import com.boardservice.exception.DuplicateBookmarkException;
import com.boardservice.exception.DuplicateLikeException;
import com.boardservice.exception.GlobalExceptionHandler;
import com.boardservice.service.PostInteractionService;

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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PostInteractionController 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class PostInteractionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PostInteractionService postInteractionService;

    @InjectMocks
    private PostInteractionController postInteractionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postInteractionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/like - 좋아요 추가 성공")
    void likePost_Success() throws Exception {
        // given
        LikeResponse response = LikeResponse.builder()
                .id(1L)
                .postId(1L)
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        // 서비스 메서드: likePost(userId, postId)
        given(postInteractionService.likePost(any(), eq(1L))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts/1/like"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.postId").value(1));
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/like - 중복 좋아요 실패")
    void likePost_Fail_Duplicate() throws Exception {
        // given
        given(postInteractionService.likePost(any(), eq(1L)))
                .willThrow(new DuplicateLikeException("이미 좋아요한 게시글입니다."));

        // when & then
        mockMvc.perform(post("/api/posts/1/like"))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/posts/{postId}/like - 좋아요 취소 성공")
    void unlikePost_Success() throws Exception {
        // given
        // 서비스 메서드: unlikePost(userId, postId)
        doNothing().when(postInteractionService).unlikePost(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/posts/1/like"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("좋아요가 취소되었습니다."));
    }

    @Test
    @DisplayName("GET /api/posts/{postId}/like/status - 좋아요 상태 조회")
    void getLikeStatus_Success() throws Exception {
        // given
        // 서비스 메서드: isLikedByUser(userId, postId)
        given(postInteractionService.isLikedByUser(any(), eq(1L))).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/posts/1/like/status"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("GET /api/posts/likes/me - 내가 좋아요한 게시글 목록")
    void getMyLikes_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        LikeResponse response = LikeResponse.builder()
                .id(1L)
                .postId(1L)
                .userId(1L)
                .build();

        given(postInteractionService.getUserLikes(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts/likes/me")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].postId").value(1));
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/bookmark - 북마크 추가 성공")
    void bookmarkPost_Success() throws Exception {
        // given
        BookmarkResponse response = BookmarkResponse.builder()
                .id(1L)
                .postId(1L)
                .userId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        // 서비스 메서드: bookmarkPost(userId, postId)
        given(postInteractionService.bookmarkPost(any(), eq(1L))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/posts/1/bookmark"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.postId").value(1));
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/bookmark - 중복 북마크 실패")
    void bookmarkPost_Fail_Duplicate() throws Exception {
        // given
        given(postInteractionService.bookmarkPost(any(), eq(1L)))
                .willThrow(new DuplicateBookmarkException("이미 북마크한 게시글입니다."));

        // when & then
        mockMvc.perform(post("/api/posts/1/bookmark"))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/posts/{postId}/bookmark - 북마크 취소 성공")
    void unbookmarkPost_Success() throws Exception {
        // given
        // 서비스 메서드: unbookmarkPost(userId, postId)
        doNothing().when(postInteractionService).unbookmarkPost(any(), eq(1L));

        // when & then
        mockMvc.perform(delete("/api/posts/1/bookmark"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("북마크가 취소되었습니다."));
    }

    @Test
    @DisplayName("GET /api/posts/{postId}/bookmark/status - 북마크 상태 조회")
    void getBookmarkStatus_Success() throws Exception {
        // given
        // 서비스 메서드: isBookmarkedByUser(userId, postId)
        given(postInteractionService.isBookmarkedByUser(any(), eq(1L))).willReturn(false);

        // when & then
        mockMvc.perform(get("/api/posts/1/bookmark/status"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("GET /api/posts/bookmarks/me - 내가 북마크한 게시글 목록")
    void getMyBookmarks_Success() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        BookmarkResponse response = BookmarkResponse.builder()
                .id(1L)
                .postId(1L)
                .userId(1L)
                .build();

        given(postInteractionService.getUserBookmarks(any(), any(Pageable.class)))
                .willReturn(new PageImpl<>(Arrays.asList(response), pageable, 1));

        // when & then
        mockMvc.perform(get("/api/posts/bookmarks/me")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].postId").value(1));
    }

    @Test
    @DisplayName("POST /api/posts/{postId}/share - 게시글 공유 성공")
    void sharePost_Success() throws Exception {
        // given
        // 서비스 메서드: sharePost(userId, postId, shareType)
        doNothing().when(postInteractionService).sharePost(any(), eq(1L), eq(ShareType.LINK));

        // when & then
        mockMvc.perform(post("/api/posts/1/share")
                        .param("shareType", "LINK"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글이 공유되었습니다."));
    }

    @Test
    @DisplayName("GET /api/posts/{postId}/stats - 게시글 통계 조회")
    void getPostStats_Success() throws Exception {
        // given
        PostStatsResponse response = PostStatsResponse.builder()
                .postId(1L)
                .viewCount(100L)
                .likeCount(50)
                .commentCount(30)
                .bookmarkCount(20)
                .shareCount(10)
                .isLiked(true)
                .isBookmarked(false)
                .build();

        // 서비스 메서드: getPostStats(postId, userId) - 순서 다름!
        given(postInteractionService.getPostStats(eq(1L), any())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/posts/1/stats"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postId").value(1))
                .andExpect(jsonPath("$.data.viewCount").value(100))
                .andExpect(jsonPath("$.data.isLiked").value(true))
                .andExpect(jsonPath("$.data.isBookmarked").value(false));
    }
}
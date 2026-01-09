package com.boardservice.service;

import java.util.Arrays;
import java.util.Optional;

import com.boardservice.dto.interaction.BookmarkResponse;
import com.boardservice.dto.interaction.LikeResponse;
import com.boardservice.dto.interaction.PostStatsResponse;
import com.boardservice.entity.BoardCategory;
import com.boardservice.entity.Bookmark;
import com.boardservice.entity.Post;
import com.boardservice.entity.PostLike;
import com.boardservice.entity.ShareType;
import com.boardservice.exception.DuplicateBookmarkException;
import com.boardservice.exception.DuplicateLikeException;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.BookmarkRepository;
import com.boardservice.repository.PostLikeRepository;
import com.boardservice.repository.PostRepository;
import com.boardservice.repository.PostShareRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * PostInteractionService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class PostInteractionServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private PostShareRepository postShareRepository;

    @InjectMocks
    private PostInteractionService postInteractionService;

    private Post post;
    private PostLike postLike;
    private Bookmark bookmark;

    @BeforeEach
    void setUp() {
        BoardCategory boardCategory = BoardCategory.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .isActive(true)
                .postCount(0)
                .build();

        post = Post.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(1L)
                .board(boardCategory)
                .build();

        postLike = PostLike.builder()
                .id(1L)
                .post(post)
                .userId(1L)
                .build();

        bookmark = Bookmark.builder()
                .id(1L)
                .post(post)
                .userId(1L)
                .build();
    }

    @Test
    @DisplayName("좋아요 추가 성공")
    void likePost_Success() {
        // given
        given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(false);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.save(any(PostLike.class))).willReturn(postLike);

        // when
        LikeResponse response = postInteractionService.likePost(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(1L);

        verify(postLikeRepository).existsByPostIdAndUserId(1L, 1L);
        verify(postRepository).findById(1L);
        verify(postLikeRepository).save(any(PostLike.class));
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 중복 좋아요")
    void likePost_Fail_Duplicate() {
        // given
        given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> postInteractionService.likePost(1L, 1L))
                .isInstanceOf(DuplicateLikeException.class)
                .hasMessageContaining("이미 좋아요한 게시글입니다");

        verify(postLikeRepository).existsByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("좋아요 추가 실패 - 존재하지 않는 게시글")
    void likePost_Fail_PostNotFound() {
        // given
        given(postLikeRepository.existsByPostIdAndUserId(999L, 1L)).willReturn(false);
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postInteractionService.likePost(1L, 999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(postRepository).findById(999L);
    }

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlikePost_Success() {
        // given
        given(postLikeRepository.findByPostIdAndUserId(1L, 1L)).willReturn(Optional.of(postLike));

        // when
        postInteractionService.unlikePost(1L, 1L);

        // then
        verify(postLikeRepository).findByPostIdAndUserId(1L, 1L);
        verify(postLikeRepository).delete(postLike);
    }

    @Test
    @DisplayName("좋아요 취소 실패 - 좋아요하지 않은 게시글")
    void unlikePost_Fail_NotLiked() {
        // given
        given(postLikeRepository.findByPostIdAndUserId(1L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postInteractionService.unlikePost(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("좋아요하지 않은 게시글입니다");

        verify(postLikeRepository).findByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("좋아요 상태 조회")
    void isLikedByUser_Success() {
        // given
        given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);

        // when
        boolean isLiked = postInteractionService.isLikedByUser(1L, 1L);

        // then
        assertThat(isLiked).isTrue();

        verify(postLikeRepository).existsByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("사용자의 좋아요 목록 조회")
    void getUserLikes_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<PostLike> likePage = new PageImpl<>(Arrays.asList(postLike), pageable, 1);

        given(postLikeRepository.findByUserIdWithPost(1L, pageable)).willReturn(likePage);

        // when
        Page<LikeResponse> responses = postInteractionService.getUserLikes(1L, pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);

        verify(postLikeRepository).findByUserIdWithPost(1L, pageable);
    }

    @Test
    @DisplayName("북마크 추가 성공")
    void bookmarkPost_Success() {
        // given
        given(bookmarkRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(false);
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(bookmarkRepository.save(any(Bookmark.class))).willReturn(bookmark);

        // when
        BookmarkResponse response = postInteractionService.bookmarkPost(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPostId()).isEqualTo(1L);

        verify(bookmarkRepository).existsByPostIdAndUserId(1L, 1L);
        verify(postRepository).findById(1L);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    @DisplayName("북마크 추가 실패 - 중복 북마크")
    void bookmarkPost_Fail_Duplicate() {
        // given
        given(bookmarkRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> postInteractionService.bookmarkPost(1L, 1L))
                .isInstanceOf(DuplicateBookmarkException.class)
                .hasMessageContaining("이미 북마크한 게시글입니다");

        verify(bookmarkRepository).existsByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("북마크 추가 실패 - 존재하지 않는 게시글")
    void bookmarkPost_Fail_PostNotFound() {
        // given
        given(bookmarkRepository.existsByPostIdAndUserId(999L, 1L)).willReturn(false);
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postInteractionService.bookmarkPost(1L, 999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(postRepository).findById(999L);
    }

    @Test
    @DisplayName("북마크 취소 성공")
    void unbookmarkPost_Success() {
        // given
        given(bookmarkRepository.findByPostIdAndUserId(1L, 1L)).willReturn(Optional.of(bookmark));

        // when
        postInteractionService.unbookmarkPost(1L, 1L);

        // then
        verify(bookmarkRepository).findByPostIdAndUserId(1L, 1L);
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    @DisplayName("북마크 취소 실패 - 북마크하지 않은 게시글")
    void unbookmarkPost_Fail_NotBookmarked() {
        // given
        given(bookmarkRepository.findByPostIdAndUserId(1L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postInteractionService.unbookmarkPost(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("북마크하지 않은 게시글입니다");

        verify(bookmarkRepository).findByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("북마크 상태 조회")
    void isBookmarkedByUser_Success() {
        // given
        given(bookmarkRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);

        // when
        boolean isBookmarked = postInteractionService.isBookmarkedByUser(1L, 1L);

        // then
        assertThat(isBookmarked).isTrue();

        verify(bookmarkRepository).existsByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("사용자의 북마크 목록 조회")
    void getUserBookmarks_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Bookmark> bookmarkPage = new PageImpl<>(Arrays.asList(bookmark), pageable, 1);

        given(bookmarkRepository.findByUserIdWithPost(1L, pageable)).willReturn(bookmarkPage);

        // when
        Page<BookmarkResponse> responses = postInteractionService.getUserBookmarks(1L, pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);

        verify(bookmarkRepository).findByUserIdWithPost(1L, pageable);
    }

    @Test
    @DisplayName("게시글 공유 성공")
    void sharePost_Success() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        postInteractionService.sharePost(1L, 1L, ShareType.LINK);

        // then
        verify(postRepository).findById(1L);
        verify(postShareRepository).save(any());
    }

    @Test
    @DisplayName("게시글 통계 조회 - 로그인 사용자")
    void getPostStats_Success_WithUser() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);
        given(bookmarkRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(false);

        // when
        PostStatsResponse response = postInteractionService.getPostStats(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsLiked()).isTrue();
        assertThat(response.getIsBookmarked()).isFalse();

        verify(postRepository).findById(1L);
        verify(postLikeRepository).existsByPostIdAndUserId(1L, 1L);
        verify(bookmarkRepository).existsByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("게시글 통계 조회 - 비로그인 사용자")
    void getPostStats_Success_WithoutUser() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        PostStatsResponse response = postInteractionService.getPostStats(1L, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsLiked()).isFalse();
        assertThat(response.getIsBookmarked()).isFalse();

        verify(postRepository).findById(1L);
    }
}

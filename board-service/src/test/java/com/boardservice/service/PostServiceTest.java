package com.boardservice.service;

import java.util.Arrays;
import java.util.Optional;

import com.boardservice.client.UserServiceClient;
import com.boardservice.dto.post.CreatePostRequest;
import com.boardservice.dto.post.PostDetailResponse;
import com.boardservice.dto.post.PostResponse;
import com.boardservice.dto.post.UpdatePostRequest;
import com.boardservice.entity.BoardCategory;
import com.boardservice.entity.Post;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.BoardCategoryRepository;
import com.boardservice.repository.BookmarkRepository;
import com.boardservice.repository.PostLikeRepository;
import com.boardservice.repository.PostRepository;
import com.common.dto.ApiResponse;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * PostService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardCategoryRepository boardCategoryRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PostService postService;

    private BoardCategory boardCategory;
    private Post post;
    private CreatePostRequest createRequest;
    private UpdatePostRequest updateRequest;
    private UserServiceClient.UserResponse userResponse;

    @BeforeEach
    void setUp() {
        boardCategory = BoardCategory.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .isActive(true)
                .postCount(0)
                .build();

        post = Post.builder()
                .id(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .userId(1L)
                .board(boardCategory)
                .build();

        createRequest = CreatePostRequest.builder()
                .boardId(1L)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        updateRequest = UpdatePostRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        userResponse = new UserServiceClient.UserResponse();
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        // given
        given(boardCategoryRepository.findById(1L)).willReturn(Optional.of(boardCategory));
        given(postRepository.save(any(Post.class))).willReturn(post);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        PostResponse response = postService.createPost(1L, createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).contains("테스트 내용");

        verify(boardCategoryRepository).findById(1L);
        verify(postRepository).save(any(Post.class));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    @DisplayName("게시글 생성 실패 - 존재하지 않는 게시판")
    void createPost_Fail_BoardNotFound() {
        // given
        given(boardCategoryRepository.findById(999L)).willReturn(Optional.empty());

        CreatePostRequest invalidRequest = CreatePostRequest.builder()
                .boardId(999L)
                .title("제목")
                .content("내용")
                .build();

        // when & then
        assertThatThrownBy(() -> postService.createPost(1L, invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시판을 찾을 수 없습니다");

        verify(boardCategoryRepository).findById(999L);
    }

    @Test
    @DisplayName("전체 게시글 목록 조회")
    void getAllPosts_Success() {
        // given
        Post post2 = Post.builder()
                .id(2L)
                .title("두번째 게시글")
                .content("두번째 내용")
                .userId(1L)
                .board(boardCategory)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post, post2), pageable, 2);

        given(postRepository.findAllByOrderByCreatedAtDesc(pageable)).willReturn(postPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<PostResponse> responses = postService.getAllPosts(pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(2);
        assertThat(responses.getContent()).hasSize(2);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("테스트 제목");

        verify(postRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("게시판별 게시글 목록 조회")
    void getPostsByBoard_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post), pageable, 1);

        given(postRepository.findByBoardIdOrderByCreatedAtDesc(1L, pageable)).willReturn(postPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<PostResponse> responses = postService.getPostsByBoard(1L, pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getContent().get(0).getTitle()).isEqualTo("테스트 제목");

        verify(postRepository).findByBoardIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    @DisplayName("사용자별 게시글 목록 조회")
    void getPostsByUser_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post), pageable, 1);

        given(postRepository.findByUserIdOrderByCreatedAtDesc(1L, pageable)).willReturn(postPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<PostResponse> responses = postService.getPostsByUser(1L, pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);

        verify(postRepository).findByUserIdOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 - 로그인 사용자")
    void getPost_Success_WithUser() {
        // given
        given(postRepository.findByIdWithBoard(1L)).willReturn(Optional.of(post));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);
        given(postLikeRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(true);
        given(bookmarkRepository.existsByPostIdAndUserId(1L, 1L)).willReturn(false);

        // when
        PostDetailResponse response = postService.getPost(1L, 1L);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getIsLiked()).isTrue();
        assertThat(response.getIsBookmarked()).isFalse();

        verify(postRepository).findByIdWithBoard(1L);
        verify(postLikeRepository).existsByPostIdAndUserId(1L, 1L);
        verify(bookmarkRepository).existsByPostIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("게시글 상세 조회 성공 - 비로그인 사용자")
    void getPost_Success_WithoutUser() {
        // given
        given(postRepository.findByIdWithBoard(1L)).willReturn(Optional.of(post));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        PostDetailResponse response = postService.getPost(1L, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getIsLiked()).isFalse();
        assertThat(response.getIsBookmarked()).isFalse();

        verify(postRepository).findByIdWithBoard(1L);
    }

    @Test
    @DisplayName("게시글 상세 조회 실패 - 존재하지 않는 게시글")
    void getPost_Fail_NotFound() {
        // given
        given(postRepository.findByIdWithBoard(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.getPost(999L, 1L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(postRepository).findByIdWithBoard(999L);
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_Success() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        PostResponse response = postService.updatePost(1L, 1L, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).contains("수정된 내용");

        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 존재하지 않는 게시글")
    void updatePost_Fail_NotFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.updatePost(1L, 999L, updateRequest))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(postRepository).findById(999L);
    }

    @Test
    @DisplayName("게시글 수정 실패 - 작성자가 아님")
    void updatePost_Fail_Unauthorized() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postService.updatePost(2L, 1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 작성자만 수정할 수 있습니다");

        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_Success() {
        // given
        given(postRepository.findByIdWithBoard(1L)).willReturn(Optional.of(post));

        // when
        postService.deletePost(1L, 1L);

        // then
        verify(postRepository).findByIdWithBoard(1L);
        verify(postRepository).delete(post);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 존재하지 않는 게시글")
    void deletePost_Fail_NotFound() {
        // given
        given(postRepository.findByIdWithBoard(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.deletePost(1L, 999L))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(postRepository).findByIdWithBoard(999L);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 작성자가 아님")
    void deletePost_Fail_Unauthorized() {
        // given
        given(postRepository.findByIdWithBoard(1L)).willReturn(Optional.of(post));

        // when & then
        assertThatThrownBy(() -> postService.deletePost(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("게시글 작성자만 삭제할 수 있습니다");

        verify(postRepository).findByIdWithBoard(1L);
    }

    @Test
    @DisplayName("인기 게시글 조회 - 좋아요 기준")
    void getPopularPostsByLikes_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post), pageable, 1);

        given(postRepository.findAllByOrderByLikeCountDescCreatedAtDesc(pageable)).willReturn(postPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<PostResponse> responses = postService.getPopularPostsByLikes(pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);

        verify(postRepository).findAllByOrderByLikeCountDescCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("인기 게시글 조회 - 조회수 기준")
    void getPopularPostsByViews_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post), pageable, 1);

        given(postRepository.findAllByOrderByViewCountDescCreatedAtDesc(pageable)).willReturn(postPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<PostResponse> responses = postService.getPopularPostsByViews(pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);

        verify(postRepository).findAllByOrderByViewCountDescCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("게시글 검색")
    void searchPosts_Success() {
        // given
        String keyword = "테스트";
        Pageable pageable = PageRequest.of(0, 20);
        Page<Post> postPage = new PageImpl<>(Arrays.asList(post), pageable, 1);

        given(postRepository.searchByKeyword(keyword, pageable)).willReturn(postPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<PostResponse> responses = postService.searchPosts(keyword, pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getContent().get(0).getTitle()).contains("테스트");

        verify(postRepository).searchByKeyword(keyword, pageable);
    }

    @Test
    @DisplayName("UserService 호출 실패 시 Unknown 반환")
    void getUsername_Fail_ReturnsUnknown() {
        // given
        given(postRepository.findByIdWithBoard(1L)).willReturn(Optional.of(post));
        given(userServiceClient.getUser(anyLong())).willThrow(new RuntimeException("UserService unavailable"));

        // when
        PostDetailResponse response = postService.getPost(1L, null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("Unknown");

        verify(userServiceClient).getUser(1L);
    }
}

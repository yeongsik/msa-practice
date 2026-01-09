package com.boardservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.boardservice.client.UserServiceClient;
import com.boardservice.dto.comment.CommentResponse;
import com.boardservice.dto.comment.CreateCommentRequest;
import com.boardservice.dto.comment.UpdateCommentRequest;
import com.boardservice.entity.BoardCategory;
import com.boardservice.entity.Comment;
import com.boardservice.entity.Post;
import com.boardservice.exception.CommentNotFoundException;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.CommentRepository;
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
 * CommentService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentService commentService;

    private Post post;
    private Comment comment;
    private CreateCommentRequest createRequest;
    private UpdateCommentRequest updateRequest;
    private UserServiceClient.UserResponse userResponse;

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

        comment = Comment.builder()
                .id(1L)
                .content("테스트 댓글")
                .userId(1L)
                .post(post)
                .build();

        createRequest = CreateCommentRequest.builder()
                .content("테스트 댓글")
                .build();

        updateRequest = UpdateCommentRequest.builder()
                .content("수정된 댓글")
                .build();

        userResponse = new UserServiceClient.UserResponse();
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createComment_Success() {
        // given
        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        CommentResponse response = commentService.createComment(1L, 1L, createRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("테스트 댓글");

        verify(postRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
        verify(userServiceClient).getUser(1L);
    }

    @Test
    @DisplayName("댓글 작성 실패 - 존재하지 않는 게시글")
    void createComment_Fail_PostNotFound() {
        // given
        given(postRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.createComment(1L, 999L, createRequest))
                .isInstanceOf(PostNotFoundException.class)
                .hasMessageContaining("게시글을 찾을 수 없습니다");

        verify(postRepository).findById(999L);
    }

    @Test
    @DisplayName("게시글의 댓글 목록 조회")
    void getComments_Success() {
        // given
        Comment comment2 = Comment.builder()
                .id(2L)
                .content("두번째 댓글")
                .userId(2L)
                .post(post)
                .build();

        given(commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(1L))
                .willReturn(Arrays.asList(comment, comment2));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        List<CommentResponse> responses = commentService.getComments(1L);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getContent()).isEqualTo("테스트 댓글");
        assertThat(responses.get(1).getContent()).isEqualTo("두번째 댓글");

        verify(commentRepository).findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(1L);
    }

    @Test
    @DisplayName("사용자별 댓글 목록 조회")
    void getCommentsByUser_Success() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Comment> commentPage = new PageImpl<>(Arrays.asList(comment), pageable, 1);

        given(commentRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(1L, pageable))
                .willReturn(commentPage);
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        Page<CommentResponse> responses = commentService.getCommentsByUser(1L, pageable);

        // then
        assertThat(responses).isNotNull();
        assertThat(responses.getTotalElements()).isEqualTo(1);
        assertThat(responses.getContent().get(0).getContent()).isEqualTo("테스트 댓글");

        verify(commentRepository).findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(1L, pageable);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));
        ApiResponse<UserServiceClient.UserResponse> mockApiResponse = ApiResponse.success(userResponse);
        given(userServiceClient.getUser(anyLong())).willReturn(mockApiResponse);

        // when
        CommentResponse response = commentService.updateComment(1L, 1L, updateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("수정된 댓글");

        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    void updateComment_Fail_NotFound() {
        // given
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, 999L, updateRequest))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");

        verify(commentRepository).findById(999L);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 작성자가 아님")
    void updateComment_Fail_Unauthorized() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(2L, 1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("댓글 작성자만 수정할 수 있습니다");

        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("댓글 수정 실패 - 삭제된 댓글")
    void updateComment_Fail_AlreadyDeleted() {
        // given
        comment.delete();
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(1L, 1L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("삭제된 댓글은 수정할 수 없습니다");

        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 성공 (소프트 삭제)")
    void deleteComment_Success() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(1L, 1L);

        // then
        assertThat(comment.getIsDeleted()).isTrue();
        assertThat(comment.getContent()).isEqualTo("삭제된 댓글입니다.");

        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 존재하지 않는 댓글")
    void deleteComment_Fail_NotFound() {
        // given
        given(commentRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(1L, 999L))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining("댓글을 찾을 수 없습니다");

        verify(commentRepository).findById(999L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 작성자가 아님")
    void deleteComment_Fail_Unauthorized() {
        // given
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(2L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("댓글 작성자만 삭제할 수 있습니다");

        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 이미 삭제된 댓글")
    void deleteComment_Fail_AlreadyDeleted() {
        // given
        comment.delete();
        given(commentRepository.findById(1L)).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> commentService.deleteComment(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 삭제된 댓글입니다");

        verify(commentRepository).findById(1L);
    }

    @Test
    @DisplayName("UserService 호출 실패 시 Unknown 반환")
    void getUsername_Fail_ReturnsUnknown() {
        // given
        given(commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(1L))
                .willReturn(Arrays.asList(comment));
        given(userServiceClient.getUser(anyLong())).willThrow(new RuntimeException("UserService unavailable"));

        // when
        List<CommentResponse> responses = commentService.getComments(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getUsername()).isEqualTo("Unknown");

        verify(userServiceClient).getUser(1L);
    }
}

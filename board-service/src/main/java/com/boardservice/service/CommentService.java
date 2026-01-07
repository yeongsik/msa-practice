package com.boardservice.service;

import com.boardservice.client.UserServiceClient;
import com.boardservice.dto.comment.CommentResponse;
import com.boardservice.dto.comment.CreateCommentRequest;
import com.boardservice.dto.comment.UpdateCommentRequest;
import com.boardservice.entity.Comment;
import com.boardservice.entity.Post;
import com.boardservice.exception.CommentNotFoundException;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.CommentRepository;
import com.boardservice.repository.PostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 댓글 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    /**
     * 댓글 작성.
     */
    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CreateCommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .userId(userId)
                .post(post)
                .build();

        Comment savedComment = commentRepository.save(comment);

        post.incrementCommentCount();

        String username = getUsername(userId);
        log.info("댓글 작성 완료: commentId={}, postId={}, userId={}",
                savedComment.getId(), postId, userId);

        return CommentResponse.from(savedComment, username);
    }

    /**
     * 게시글의 댓글 목록 조회.
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        List<Comment> comments = commentRepository
                .findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    String username = getUsername(comment.getUserId());
                    return CommentResponse.from(comment, username);
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 댓글 목록 조회.
     */
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByUser(Long userId, Pageable pageable) {
        Page<Comment> comments = commentRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId, pageable);

        return comments.map(comment -> {
            String username = getUsername(comment.getUserId());
            return CommentResponse.from(comment, username);
        });
    }

    /**
     * 댓글 수정.
     */
    @Transactional
    public CommentResponse updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(
                        "댓글을 찾을 수 없습니다. id=" + commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자만 수정할 수 있습니다.");
        }

        if (comment.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.update(request.getContent());
        String username = getUsername(userId);

        log.info("댓글 수정 완료: commentId={}, userId={}", commentId, userId);

        return CommentResponse.from(comment, username);
    }

    /**
     * 댓글 삭제 (소프트 삭제).
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(
                        "댓글을 찾을 수 없습니다. id=" + commentId));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }

        if (comment.getIsDeleted()) {
            throw new IllegalArgumentException("이미 삭제된 댓글입니다.");
        }

        comment.delete();

        comment.getPost().decrementCommentCount();

        log.info("댓글 삭제 완료 (소프트 삭제): commentId={}, userId={}", commentId, userId);
    }

    /**
     * User Service로부터 username 조회 (Feign Client).
     */
    private String getUsername(Long userId) {
        try {
            return userServiceClient.getUser(userId).getData().getUsername();
        } catch (Exception e) {
            log.error("Failed to fetch username for userId: {}", userId, e);
            return "Unknown";
        }
    }
}

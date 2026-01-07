package com.boardservice.controller;

import com.boardservice.dto.comment.CommentResponse;
import com.boardservice.dto.comment.CreateCommentRequest;
import com.boardservice.dto.comment.UpdateCommentRequest;
import com.boardservice.service.CommentService;
import com.common.dto.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 댓글 컨트롤러.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성.
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateCommentRequest request) {
        log.info("댓글 작성 요청: postId={}, userId={}", postId, userId);
        CommentResponse response = commentService.createComment(userId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 게시글의 댓글 목록 조회.
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long postId) {
        List<CommentResponse> responses = commentService.getComments(postId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 내가 작성한 댓글 목록 조회.
     */
    @GetMapping("/comments/users/me")
    public ResponseEntity<ApiResponse<Page<CommentResponse>>> getMyComments(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<CommentResponse> responses = commentService.getCommentsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 댓글 수정.
     */
    @PutMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateCommentRequest request) {
        log.info("댓글 수정 요청: commentId={}, userId={}", id, userId);
        CommentResponse response = commentService.updateComment(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 댓글 삭제 (소프트 삭제).
     */
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        log.info("댓글 삭제 요청: commentId={}, userId={}", id, userId);
        commentService.deleteComment(userId, id);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다.", null));
    }
}

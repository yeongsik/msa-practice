package com.boardservice.controller;

import com.boardservice.dto.post.CreatePostRequest;
import com.boardservice.dto.post.PostDetailResponse;
import com.boardservice.dto.post.PostResponse;
import com.boardservice.dto.post.UpdatePostRequest;
import com.boardservice.service.PostService;
import com.boardservice.service.ViewCountService;
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

/**
 * 게시글 컨트롤러.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final ViewCountService viewCountService;

    /**
     * 게시글 작성.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        log.info("게시글 작성 요청: userId={}, boardId={}", userId, request.getBoardId());
        PostResponse response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 전체 게시글 목록 조회 (페이징).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.Direction.fromString(direction), sort);

        Page<PostResponse> responses = postService.getAllPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시판별 게시글 목록 조회.
     */
    @GetMapping("/board-categories/{boardId}")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByBoard(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<PostResponse> responses = postService.getPostsByBoard(boardId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 내가 작성한 게시글 목록 조회.
     */
    @GetMapping("/users/me")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getMyPosts(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<PostResponse> responses = postService.getPostsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시글 상세 조회 (조회수 증가).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal(errorOnInvalidType = false) Long userId) {

        PostDetailResponse response = postService.getPost(id, userId);

        viewCountService.incrementViewCount(id, userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시글 수정.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdatePostRequest request) {
        log.info("게시글 수정 요청: postId={}, userId={}", id, userId);
        PostResponse response = postService.updatePost(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시글 삭제.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        log.info("게시글 삭제 요청: postId={}, userId={}", id, userId);
        postService.deletePost(userId, id);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }

    /**
     * 인기 게시글 조회 (좋아요 기준).
     */
    @GetMapping("/popular/likes")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPopularPostsByLikes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostResponse> responses = postService.getPopularPostsByLikes(pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 인기 게시글 조회 (조회수 기준).
     */
    @GetMapping("/popular/views")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPopularPostsByViews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostResponse> responses = postService.getPopularPostsByViews(pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시글 검색.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<PostResponse> responses = postService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}

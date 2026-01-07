package com.boardservice.controller;

import com.boardservice.dto.interaction.BookmarkResponse;
import com.boardservice.dto.interaction.LikeResponse;
import com.boardservice.dto.interaction.PostStatsResponse;
import com.boardservice.entity.ShareType;
import com.boardservice.service.PostInteractionService;
import com.common.dto.ApiResponse;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 게시글 인터랙션 컨트롤러 (좋아요, 북마크, 공유).
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    /**
     * 좋아요 추가.
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        log.info("좋아요 추가 요청: postId={}, userId={}", postId, userId);
        LikeResponse response = postInteractionService.likePost(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 좋아요 취소.
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        log.info("좋아요 취소 요청: postId={}, userId={}", postId, userId);
        postInteractionService.unlikePost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("좋아요가 취소되었습니다.", null));
    }

    /**
     * 좋아요 상태 조회.
     */
    @GetMapping("/{postId}/like/status")
    public ResponseEntity<ApiResponse<Boolean>> getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        boolean isLiked = postInteractionService.isLikedByUser(userId, postId);
        return ResponseEntity.ok(ApiResponse.success(isLiked));
    }

    /**
     * 내가 좋아요한 게시글 목록.
     */
    @GetMapping("/likes/me")
    public ResponseEntity<ApiResponse<Page<LikeResponse>>> getMyLikes(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<LikeResponse> responses = postInteractionService.getUserLikes(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 북마크 추가.
     */
    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<ApiResponse<BookmarkResponse>> bookmarkPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        log.info("북마크 추가 요청: postId={}, userId={}", postId, userId);
        BookmarkResponse response = postInteractionService.bookmarkPost(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 북마크 취소.
     */
    @DeleteMapping("/{postId}/bookmark")
    public ResponseEntity<ApiResponse<Void>> unbookmarkPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        log.info("북마크 취소 요청: postId={}, userId={}", postId, userId);
        postInteractionService.unbookmarkPost(userId, postId);
        return ResponseEntity.ok(ApiResponse.success("북마크가 취소되었습니다.", null));
    }

    /**
     * 북마크 상태 조회.
     */
    @GetMapping("/{postId}/bookmark/status")
    public ResponseEntity<ApiResponse<Boolean>> getBookmarkStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId) {
        boolean isBookmarked = postInteractionService.isBookmarkedByUser(userId, postId);
        return ResponseEntity.ok(ApiResponse.success(isBookmarked));
    }

    /**
     * 내가 북마크한 게시글 목록.
     */
    @GetMapping("/bookmarks/me")
    public ResponseEntity<ApiResponse<Page<BookmarkResponse>>> getMyBookmarks(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<BookmarkResponse> responses = postInteractionService.getUserBookmarks(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시글 공유.
     */
    @PostMapping("/{postId}/share")
    public ResponseEntity<ApiResponse<Void>> sharePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "LINK") ShareType shareType) {
        log.info("게시글 공유 요청: postId={}, userId={}, shareType={}", postId, userId, shareType);
        postInteractionService.sharePost(userId, postId, shareType);
        return ResponseEntity.ok(ApiResponse.success("게시글이 공유되었습니다.", null));
    }

    /**
     * 게시글 통계 조회.
     */
    @GetMapping("/{postId}/stats")
    public ResponseEntity<ApiResponse<PostStatsResponse>> getPostStats(
            @PathVariable Long postId,
            @AuthenticationPrincipal(errorOnInvalidType = false) Long userId) {
        PostStatsResponse response = postInteractionService.getPostStats(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}

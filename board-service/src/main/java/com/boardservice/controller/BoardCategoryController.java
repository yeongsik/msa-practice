package com.boardservice.controller;

import com.boardservice.dto.board.BoardCategoryResponse;
import com.boardservice.dto.board.CreateBoardCategoryRequest;
import com.boardservice.service.BoardCategoryService;
import com.common.dto.ApiResponse;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 게시판 카테고리 컨트롤러.
 */
@RestController
@RequestMapping("/api/board-categories")
@RequiredArgsConstructor
@Slf4j
public class BoardCategoryController {

    private final BoardCategoryService boardCategoryService;

    /**
     * 게시판 생성.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BoardCategoryResponse>> createBoardCategory(
            @Valid @RequestBody CreateBoardCategoryRequest request) {
        log.info("게시판 생성 요청: name={}", request.getName());
        BoardCategoryResponse response = boardCategoryService.createBoardCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 전체 게시판 목록 조회.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardCategoryResponse>>> getAllBoardCategories() {
        List<BoardCategoryResponse> responses = boardCategoryService.getAllBoardCategories();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 활성화된 게시판 목록 조회.
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<BoardCategoryResponse>>> getActiveBoardCategories() {
        List<BoardCategoryResponse> responses = boardCategoryService.getActiveBoardCategories();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 게시판 단건 조회.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardCategoryResponse>> getBoardCategory(
            @PathVariable Long id) {
        BoardCategoryResponse response = boardCategoryService.getBoardCategory(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시판 수정.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardCategoryResponse>> updateBoardCategory(
            @PathVariable Long id,
            @Valid @RequestBody CreateBoardCategoryRequest request) {
        log.info("게시판 수정 요청: id={}, name={}", id, request.getName());
        BoardCategoryResponse response = boardCategoryService.updateBoardCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 게시판 삭제.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoardCategory(@PathVariable Long id) {
        log.info("게시판 삭제 요청: id={}", id);
        boardCategoryService.deleteBoardCategory(id);
        return ResponseEntity.ok(ApiResponse.success("게시판이 삭제되었습니다.", null));
    }

    /**
     * 게시판 비활성화.
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateBoardCategory(@PathVariable Long id) {
        log.info("게시판 비활성화 요청: id={}", id);
        boardCategoryService.deactivateBoardCategory(id);
        return ResponseEntity.ok(ApiResponse.success("게시판이 비활성화되었습니다.", null));
    }

    /**
     * 게시판 활성화.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Void>> activateBoardCategory(@PathVariable Long id) {
        log.info("게시판 활성화 요청: id={}", id);
        boardCategoryService.activateBoardCategory(id);
        return ResponseEntity.ok(ApiResponse.success("게시판이 활성화되었습니다.", null));
    }
}

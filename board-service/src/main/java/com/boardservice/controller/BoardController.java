package com.boardservice.controller;

import com.boardservice.dto.BoardResponse;
import com.boardservice.dto.CreateBoardRequest;
import com.boardservice.dto.UpdateBoardRequest;
import com.boardservice.service.BoardService;
import com.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<BoardResponse>> createBoard(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateBoardRequest request
    ) {
        log.info("게시글 작성 요청: userId={}", userId);
        BoardResponse response = boardService.createBoard(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getAllBoards() {
        List<BoardResponse> responses = boardService.getAllBoards();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(@PathVariable Long id) {
        BoardResponse response = boardService.getBoard(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BoardResponse>> updateBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateBoardRequest request
    ) {
        log.info("게시글 수정 요청: boardId={}, userId={}", id, userId);
        BoardResponse response = boardService.updateBoard(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("게시글 삭제 요청: boardId={}, userId={}", id, userId);
        boardService.deleteBoard(userId, id);
        return ResponseEntity.ok(ApiResponse.success("게시글이 삭제되었습니다.", null));
    }
}

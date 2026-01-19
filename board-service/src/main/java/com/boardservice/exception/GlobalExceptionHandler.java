package com.boardservice.exception;

import com.common.dto.ApiResponse;
import com.common.exception.BaseExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Board Service 전역 예외 핸들러.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    /**
     * IllegalArgumentException 처리.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * PostNotFoundException 처리.
     */
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * CommentNotFoundException 처리.
     */
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCommentNotFoundException(CommentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * DuplicateLikeException 처리.
     */
    @ExceptionHandler(DuplicateLikeException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateLikeException(DuplicateLikeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * DuplicateBookmarkException 처리.
     */
    @ExceptionHandler(DuplicateBookmarkException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateBookmarkException(DuplicateBookmarkException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }
}

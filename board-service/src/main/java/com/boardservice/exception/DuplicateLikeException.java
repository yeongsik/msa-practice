package com.boardservice.exception;

/**
 * 이미 좋아요한 게시글에 중복으로 좋아요할 때 발생하는 예외.
 */
public class DuplicateLikeException extends RuntimeException {

    public DuplicateLikeException(String message) {
        super(message);
    }

    public DuplicateLikeException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.boardservice.exception;

/**
 * 이미 북마크한 게시글에 중복으로 북마크할 때 발생하는 예외.
 */
public class DuplicateBookmarkException extends RuntimeException {

    public DuplicateBookmarkException(String message) {
        super(message);
    }

    public DuplicateBookmarkException(String message, Throwable cause) {
        super(message, cause);
    }
}

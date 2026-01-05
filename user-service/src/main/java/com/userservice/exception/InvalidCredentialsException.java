package com.userservice.exception;

/**
 * 잘못된 자격 증명 예외 (로그인 실패)
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

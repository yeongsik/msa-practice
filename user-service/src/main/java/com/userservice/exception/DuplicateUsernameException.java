package com.userservice.exception;

/**
 * 중복된 사용자명 예외
 */
public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String username) {
        super("이미 존재하는 사용자명입니다: " + username);
    }
}
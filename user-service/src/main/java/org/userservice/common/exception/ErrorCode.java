package org.userservice.common.exception;

import lombok.Getter;

/**
 * Error codes for User Service
 */
@Getter
public enum ErrorCode {
    // User related errors
    USER_001("USER-001", "이미 존재하는 사용자명입니다."),
    USER_002("USER-002", "이미 존재하는 이메일입니다."),
    USER_003("USER-003", "사용자를 찾을 수 없습니다."),
    USER_004("USER-004", "잘못된 사용자명 형식입니다."),
    USER_005("USER-005", "잘못된 이메일 형식입니다."),
    USER_006("USER-006", "잘못된 비밀번호 형식입니다."),
    
    // System errors
    SYS_001("SYS-001", "시스템 오류가 발생했습니다."),
    SYS_002("SYS-002", "데이터베이스 오류가 발생했습니다.");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
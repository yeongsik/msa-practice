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
    USER_007("USER-007", "잘못된 로그인 정보입니다."),
    USER_008("USER-008", "이미 탈퇴한 사용자입니다."),
    
    // Authentication errors
    AUTH_001("AUTH-001", "인증이 필요합니다."),
    AUTH_002("AUTH-002", "권한이 없습니다."),
    AUTH_003("AUTH-003", "잘못된 토큰입니다."),
    AUTH_004("AUTH-004", "토큰이 만료되었습니다."),
    
    // System errors
    SYS_001("SYS-001", "시스템 오류가 발생했습니다."),
    SYS_002("SYS-002", "데이터베이스 오류가 발생했습니다."),
    
    // Token errors
    INVALID_TOKEN("AUTH-003", "잘못된 토큰입니다."),
    EXPIRED_TOKEN("AUTH-004", "토큰이 만료되었습니다.");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
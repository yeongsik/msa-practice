package org.userservice.common.exception;

import lombok.Getter;

/**
 * Business logic exception
 */

@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCode errorCode;
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getUserMessage() {
        return errorCode.getMessage();
    }
}
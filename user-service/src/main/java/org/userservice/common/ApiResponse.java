package org.userservice.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    T data,
    ErrorInfo error,
    MetaInfo meta
) {
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            true,
            data,
            null,
            new MetaInfo(LocalDateTime.now(), "v1")
        );
    }
    
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(
            false,
            null,
            new ErrorInfo(errorCode, message),
            new MetaInfo(LocalDateTime.now(), "v1")
        );
    }
    
    public record ErrorInfo(
        String code,
        String message
    ) {}
    
    public record MetaInfo(
        LocalDateTime timestamp,
        String version
    ) {}
}
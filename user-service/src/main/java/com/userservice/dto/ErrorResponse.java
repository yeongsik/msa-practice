package com.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 에러 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * 에러 메시지
     */
    private String message;

    /**
     * HTTP 상태 코드
     */
    private int status;

    /**
     * 에러 발생 경로
     */
    private String path;

    /**
     * 에러 발생 시각
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
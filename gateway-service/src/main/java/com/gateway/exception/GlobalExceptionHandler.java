package com.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Gateway 전역 예외 처리 핸들러.
 * WebFlux 기반의 Gateway에서 발생하는 에러를 가로채서 공통 JSON 포맷으로 반환합니다.
 */
@Slf4j
@Order(-1) // 우선순위를 높게 설정하여 기본 핸들러보다 먼저 실행
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 기본 에러 응답 설정
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getMessage();

        // 1. ResponseStatusException 처리 (예: 404 Not Found)
        if (ex instanceof ResponseStatusException) {
            status = HttpStatus.resolve(((ResponseStatusException) ex).getStatusCode().value());
        }

        // 2. Gateway Filter에서 발생시킨 에러 처리 (AuthorizationHeaderFilter 등)
        // HTTP Status 코드가 이미 설정된 경우 해당 코드 사용
        if (response.getStatusCode() != null) {
            status = HttpStatus.resolve(response.getStatusCode().value());
        }
        
        // 상태 코드가 null이면 500으로 설정
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // Rate Limit Exceeded (429) 처리
        if (status == HttpStatus.TOO_MANY_REQUESTS) {
            message = "요청 횟수 제한을 초과했습니다. 잠시 후 다시 시도해주세요.";
        }

        log.error("[Gateway Error] Status: {}, Message: {}", status, message);

        // 공통 에러 응답 객체 생성
        ErrorResponse errorResponse = new ErrorResponse(status.value(), message);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            response.setStatusCode(status);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error writing response", e);
            return response.setComplete();
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final int status;
        private final String message;
        private final String timestamp = LocalDateTime.now().toString();
    }
}

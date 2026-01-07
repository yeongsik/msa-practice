package com.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.common.util.JwtUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * JWT 인증 필터 (Gateway용).
 */
@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    private final ReactiveStringRedisTemplate redisTemplate;

    public AuthorizationHeaderFilter(ReactiveStringRedisTemplate redisTemplate) {
        super(Config.class);
        this.redisTemplate = redisTemplate;
    }

    public static class Config {
        // 설정 정보가 필요하다면 여기에 추가
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Authorization 헤더 존재 여부 확인
            String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
                return onError(exchange, "No Bearer token", HttpStatus.UNAUTHORIZED);
            }

            // 2. Bearer 토큰 추출
            String jwt = authorizationHeader.replace("Bearer", "").trim();

            // 3. Redis 블랙리스트 확인
            return redisTemplate.hasKey(jwt)
                    .flatMap(isBlacklisted -> {
                        if (isBlacklisted) {
                            return onError(exchange, "Token is blacklisted (Logout)", HttpStatus.UNAUTHORIZED);
                        }

                        // 4. 토큰 유효성 검증
                        if (!JwtUtil.validate(jwt)) {
                            return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
                        }

                        // 5. 하위 서비스에 userId 전달
                        Long userId = JwtUtil.getUserId(jwt);
                        ServerHttpRequest modifiedRequest = request.mutate()
                                .header("X-User-Id", String.valueOf(userId))
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    });
        };
    }

    // 에러 응답 처리 (Mono 반환)
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}
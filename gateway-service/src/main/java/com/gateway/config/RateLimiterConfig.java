package com.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Rate Limiter를 위한 KeyResolver 설정.
 * 요청을 구분하는 기준(Key)을 정의합니다.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * 사용자별 Rate Limiting을 위한 KeyResolver.
     * 인증된 사용자(X-User-Id 헤더)를 기준으로 제한합니다.
     * 비로그인 사용자는 IP 주소를 기준으로 제한합니다.
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 1. 인증된 사용자 (Gateway Filter에서 주입한 헤더)
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null) {
                return Mono.just(userId);
            }

            // 2. 비로그인 사용자 (IP 주소)
            return Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                    .getAddress().getHostAddress());
        };
    }
}

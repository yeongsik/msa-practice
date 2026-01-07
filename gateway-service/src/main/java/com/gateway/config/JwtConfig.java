package com.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import com.common.util.JwtUtil;
import jakarta.annotation.PostConstruct;

/**
 * JWT 설정 초기화 클래스.
 */
@Configuration
public class JwtConfig {

    @Value("${token.secret}")
    private String secret;

    @Value("${token.access-expiration}")
    private long accessExpiration;

    @Value("${token.refresh-expiration}")
    private long refreshExpiration;

    /**
     * JwtUtil 초기화.
     */
    @PostConstruct
    public void init() {
        JwtUtil.init(secret, accessExpiration, refreshExpiration);
    }
}

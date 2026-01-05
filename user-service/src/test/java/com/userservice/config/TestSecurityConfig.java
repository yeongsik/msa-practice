package com.userservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 테스트용 Security 설정
 * 테스트 환경에서는 모든 요청을 허용
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public WebTestClient webTestClient(ApplicationContext applicationContext) {
        return WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .build();
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}

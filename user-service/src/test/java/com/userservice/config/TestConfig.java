package com.userservice.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 테스트 설정
 */
@TestConfiguration
public class TestConfig {

    @Bean
    public WebTestClient webTestClient(ApplicationContext applicationContext) {
        return WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .build();
    }
}
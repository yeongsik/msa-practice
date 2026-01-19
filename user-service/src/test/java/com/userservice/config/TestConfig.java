package com.userservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * 테스트 설정.
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

    /**
     * 테스트용 Mock StringRedisTemplate.
     */
    @Bean
    @Primary
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate mockTemplate = Mockito.mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = Mockito.mock(ValueOperations.class);
        Mockito.when(mockTemplate.opsForValue()).thenReturn(valueOps);
        Mockito.when(valueOps.get(Mockito.anyString())).thenReturn(null);
        return mockTemplate;
    }
}
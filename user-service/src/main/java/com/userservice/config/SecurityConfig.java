package com.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 보안 설정 클래스
 * BCryptPasswordEncoder를 빈으로 등록하여 비밀번호 암호화에 사용
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 암호화를 위한 BCryptPasswordEncoder 빈 생성
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
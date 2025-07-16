package org.userservice.infrastructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 비밀번호 암호화 및 검증을 담당하는 클래스
 */
@Component("userPasswordEncoder")
@RequiredArgsConstructor
@Slf4j
public class PasswordEncoder {
    
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    
    /**
     * 비밀번호 암호화
     */
    public String encode(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }
    
    /**
     * 비밀번호 검증
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 비밀번호 강도 검증
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) {
            return false;
        }
        
        // 최소 하나의 영문자, 숫자, 특수문자 포함
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()].*");
        
        return hasLetter && hasDigit && hasSpecialChar;
    }
}
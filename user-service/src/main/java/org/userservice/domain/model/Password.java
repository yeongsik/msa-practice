package org.userservice.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 비밀번호 Value Object
 * 암호화된 비밀번호와 정책 검증을 담당
 */
public record Password(String encryptedValue) {
    
    // 비밀번호 정책: 8자 이상, 영문/숫자/특수문자 조합
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;
    
    public Password {
        Objects.requireNonNull(encryptedValue, "비밀번호는 필수값입니다");
        
        if (encryptedValue.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 빈 값일 수 없습니다");
        }
    }
    
    /**
     * 원시 비밀번호로부터 암호화된 Password 생성
     */
    public static Password createFromRaw(String rawPassword, PasswordEncoder encoder) {
        validateRawPassword(rawPassword);
        String encrypted = encoder.encode(rawPassword);
        return new Password(encrypted);
    }
    
    /**
     * 이미 암호화된 비밀번호로부터 Password 생성 (DB에서 조회시 사용)
     */
    public static Password fromEncrypted(String encryptedPassword) {
        return new Password(encryptedPassword);
    }
    
    /**
     * 원시 비밀번호 정책 검증
     */
    public static void validateRawPassword(String rawPassword) {
        Objects.requireNonNull(rawPassword, "비밀번호는 필수값입니다");
        
        if (rawPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 최소 " + MIN_LENGTH + "자 이상이어야 합니다");
        }
        
        if (rawPassword.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("비밀번호는 최대 " + MAX_LENGTH + "자 이하여야 합니다");
        }
        
        if (!PASSWORD_PATTERN.matcher(rawPassword).matches()) {
            throw new IllegalArgumentException(
                "비밀번호는 영문, 숫자, 특수문자(@$!%*?&)를 모두 포함해야 합니다"
            );
        }
    }
    
    /**
     * 비밀번호 일치 확인
     */
    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.encryptedValue);
    }
    
    /**
     * 다른 암호화된 비밀번호와 일치 확인
     */
    public boolean matches(Password other) {
        return Objects.equals(this.encryptedValue, other.encryptedValue);
    }
    
    /**
     * 비밀번호 변경 가능 여부 확인
     */
    public boolean canBeChangedTo(String newRawPassword, PasswordEncoder encoder) {
        // 현재 비밀번호와 동일한 비밀번호로 변경 불가
        if (matches(newRawPassword, encoder)) {
            return false;
        }
        
        try {
            validateRawPassword(newRawPassword);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 비밀번호 강도 평가
     */
    public static PasswordStrength evaluateStrength(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH) {
            return PasswordStrength.WEAK;
        }
        
        int score = 0;
        
        // 길이 점수
        if (rawPassword.length() >= 12) score += 2;
        else if (rawPassword.length() >= 10) score += 1;
        
        // 문자 유형 점수
        if (rawPassword.matches(".*[a-z].*")) score += 1;  // 소문자
        if (rawPassword.matches(".*[A-Z].*")) score += 1;  // 대문자  
        if (rawPassword.matches(".*\\d.*")) score += 1;    // 숫자
        if (rawPassword.matches(".*[@$!%*?&].*")) score += 1; // 특수문자
        
        // 연속 문자 감점
        if (hasSequentialChars(rawPassword)) score -= 1;
        
        if (score >= 6) return PasswordStrength.STRONG;
        if (score >= 4) return PasswordStrength.MEDIUM;
        return PasswordStrength.WEAK;
    }
    
    private static boolean hasSequentialChars(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            if (c1 + 1 == c2 && c2 + 1 == c3) {
                return true; // 연속 증가 (abc, 123)
            }
            if (c1 - 1 == c2 && c2 - 1 == c3) {
                return true; // 연속 감소 (cba, 321)
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Password{encrypted}"; // 보안상 실제 값 노출 안함
    }
    
    /**
     * 비밀번호 인코더 인터페이스
     */
    public interface PasswordEncoder {
        String encode(String rawPassword);
        boolean matches(String rawPassword, String encodedPassword);
    }
    
    /**
     * 비밀번호 강도
     */
    public enum PasswordStrength {
        WEAK("약함"),
        MEDIUM("보통"), 
        STRONG("강함");
        
        private final String description;
        
        PasswordStrength(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}
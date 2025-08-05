package org.userservice.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 이메일 Value Object
 * 이메일 주소의 유효성과 불변성을 보장
 */
public record Email(String value) {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private static final int MAX_LENGTH = 254; // RFC 5321 표준
    
    public Email {
        Objects.requireNonNull(value, "이메일은 필수값입니다");
        value = value.trim().toLowerCase(); // 정규화
        
        if (value.isEmpty()) {
            throw new IllegalArgumentException("이메일은 빈 값일 수 없습니다");
        }
        
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("이메일은 " + MAX_LENGTH + "자를 초과할 수 없습니다");
        }
        
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다: " + value);
        }
    }
    
    /**
     * 정적 팩토리 메서드
     */
    public static Email of(String email) {
        return new Email(email);
    }
    
    /**
     * 도메인 검증
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    /**
     * 로컬 부분 반환
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }
    
    /**
     * 마스킹된 이메일 반환 (프라이버시 보호)
     * 예: john@example.com -> j***@example.com
     */
    public String getMasked() {
        String localPart = getLocalPart();
        String domain = getDomain();
        
        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***@" + domain;
        }
        
        return localPart.charAt(0) + "***" + localPart.charAt(localPart.length() - 1) + "@" + domain;
    }
    
    /**
     * 특정 도메인인지 확인
     */
    public boolean hasDomain(String domain) {
        return getDomain().equalsIgnoreCase(domain);
    }
    
    @Override
    public String toString() {
        return value;
    }
}
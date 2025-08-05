package org.userservice.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 사용자명 Value Object
 * 사용자명의 유효성과 정책을 관리
 */
public record Username(String value) {
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;
    private static final String[] RESERVED_USERNAMES = {
        "admin", "root", "system", "api", "www", "mail", "ftp", 
        "support", "help", "service", "user", "null", "undefined"
    };
    
    public Username {
        Objects.requireNonNull(value, "사용자명은 필수값입니다");
        value = value.trim().toLowerCase(); // 정규화 (소문자로 통일)
        
        if (!isValid(value)) {
            throw new IllegalArgumentException("유효하지 않은 사용자명입니다: " + value);
        }
        
        if (isReserved(value)) {
            throw new IllegalArgumentException("사용할 수 없는 사용자명입니다: " + value);
        }
    }
    
    /**
     * 정적 팩토리 메서드
     */
    public static Username of(String username) {
        return new Username(username);
    }
    
    /**
     * 사용자명 유효성 검사
     */
    private static boolean isValid(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        
        if (username.length() < MIN_LENGTH || username.length() > MAX_LENGTH) {
            return false;
        }
        
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 예약된 사용자명 확인
     */
    private static boolean isReserved(String username) {
        String lowerUsername = username.toLowerCase();
        for (String reserved : RESERVED_USERNAMES) {
            if (reserved.equals(lowerUsername)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 사용자명 길이 반환
     */
    public int length() {
        return value.length();
    }
    
    /**
     * 숫자로 시작하는지 확인
     */
    public boolean startsWithNumber() {
        return Character.isDigit(value.charAt(0));
    }
    
    /**
     * 언더스코어 포함 여부
     */
    public boolean containsUnderscore() {
        return value.contains("_");
    }
    
    /**
     * 추천 대안 사용자명 생성
     */
    public static Username generateAlternative(String baseUsername) {
        String normalized = baseUsername.toLowerCase().replaceAll("[^a-zA-Z0-9_]", "");
        
        if (normalized.length() < MIN_LENGTH) {
            normalized = normalized + "123";
        }
        
        if (normalized.length() > MAX_LENGTH) {
            normalized = normalized.substring(0, MAX_LENGTH);
        }
        
        // 예약어인 경우 접미사 추가
        if (isReserved(normalized)) {
            normalized += "_user";
        }
        
        return new Username(normalized);
    }
    
    @Override
    public String toString() {
        return value;
    }
}

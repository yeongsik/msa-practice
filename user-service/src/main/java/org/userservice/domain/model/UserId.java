package org.userservice.domain.model;

import java.util.Objects;
import org.userservice.infrastructure.common.SnowflakeIdGenerator;

/**
 * 사용자 ID Value Object
 * Snowflake ID 기반의 고유 식별자
 */
public record UserId(Long value) {
    
    public UserId {
        Objects.requireNonNull(value, "사용자 ID는 필수값입니다");
        
        if (value <= 0) {
            throw new IllegalArgumentException("사용자 ID는 양수여야 합니다: " + value);
        }
    }
    
    /**
     * 정적 팩토리 메서드
     */
    public static UserId of(Long value) {
        return new UserId(value);
    }
    
    /**
     * Snowflake 알고리즘을 사용한 새로운 사용자 ID 생성
     * 분산 환경에서 고유성을 보장하는 64bit ID 생성
     */
    public static UserId generate() {
        return new UserId(SnowflakeIdGenerator.generate());
    }
    
    /**
     * 문자열로부터 UserId 생성
     */
    public static UserId fromString(String idString) {
        try {
            return new UserId(Long.parseLong(idString));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID 형식입니다: " + idString, e);
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

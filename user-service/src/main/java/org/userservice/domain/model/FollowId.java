package org.userservice.domain.model;

import java.util.Objects;
import org.userservice.infrastructure.common.SnowflakeIdGenerator;

/**
 * 팔로우 ID Value Object
 * Snowflake ID 기반의 고유 식별자
 */
public record FollowId(Long value) {
    
    public FollowId {
        Objects.requireNonNull(value, "팔로우 ID는 필수값입니다");
        
        if (value <= 0) {
            throw new IllegalArgumentException("팔로우 ID는 양수여야 합니다: " + value);
        }
    }
    
    /**
     * 정적 팩토리 메서드
     */
    public static FollowId of(Long value) {
        return new FollowId(value);
    }
    
    /**
     * Snowflake 알고리즘을 사용한 새로운 팔로우 ID 생성
     * 분산 환경에서 고유성을 보장하는 64bit ID 생성
     */
    public static FollowId generate() {
        return new FollowId(SnowflakeIdGenerator.generate());
    }
    
    /**
     * 문자열로부터 FollowId 생성
     */
    public static FollowId fromString(String idString) {
        try {
            return new FollowId(Long.parseLong(idString));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효하지 않은 팔로우 ID 형식입니다: " + idString, e);
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
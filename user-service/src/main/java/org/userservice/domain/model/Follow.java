package org.userservice.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 팔로우 관계 도메인 엔티티
 */
@Getter
public class Follow {
    
    private final FollowId id;
    private final UserId follower;    // 팔로우하는 사용자
    private final UserId following;   // 팔로우당하는 사용자
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    
    public Follow(FollowId id, UserId follower, UserId following, LocalDateTime createdAt) {
        this.id = id;
        this.follower = follower;
        this.following = following;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.deleted = false;
    }
    
    public Follow(UserId follower, UserId following) {
        this(FollowId.generate(), follower, following, LocalDateTime.now());
    }
    
    /**
     * 새로운 팔로우 관계 생성을 위한 정적 팩토리 메서드
     * 의도를 명확하게 표현하고 도메인 규칙을 중앙화
     * 
     * @param follower 팔로우하는 사용자
     * @param following 팔로우받는 사용자
     * @return 새로운 팔로우 관계
     */
    public static Follow create(UserId follower, UserId following) {
        validateFollowCreation(follower, following);
        return new Follow(follower, following);
    }
    
    /**
     * 팔로우 생성 시 도메인 규칙 검증
     */
    private static void validateFollowCreation(UserId follower, UserId following) {
        if (follower == null || following == null) {
            throw new IllegalArgumentException("팔로우 관계 생성 시 사용자 정보는 필수입니다.");
        }
        
        if (follower.equals(following)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }
    }
    
    /**
     * 팔로우 관계 삭제 (소프트 삭제)
     */
    public void unfollow() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 팔로우 관계가 활성 상태인지 확인
     */
    public boolean isActive() {
        return !deleted;
    }
    
    /**
     * 특정 사용자가 팔로워인지 확인
     */
    public boolean isFollowedBy(UserId userId) {
        return this.follower.equals(userId) && !deleted;
    }
    
    /**
     * 특정 사용자를 팔로우하고 있는지 확인
     */
    public boolean isFollowing(UserId userId) {
        return this.following.equals(userId) && !deleted;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Follow follow)) return false;
        
        return id.equals(follow.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

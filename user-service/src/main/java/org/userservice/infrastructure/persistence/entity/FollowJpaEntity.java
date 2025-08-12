package org.userservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.userservice.infrastructure.common.BaseEntity;

/**
 * 팔로우 관계 JPA 엔티티
 * 사용자 간의 팔로우 관계를 데이터베이스에 저장하기 위한 엔티티
 */
@Entity
@Table(name = "follows", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_follows_follower_following", 
                           columnNames = {"follower_id", "following_id"})
       },
       indexes = {
           @Index(name = "idx_follows_follower", columnList = "follower_id"),
           @Index(name = "idx_follows_following", columnList = "following_id"),
           @Index(name = "idx_follows_created_at", columnList = "created_at"),
           @Index(name = "idx_follows_follower_deleted", columnList = "follower_id, deleted"),
           @Index(name = "idx_follows_following_deleted", columnList = "following_id, deleted")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowJpaEntity extends BaseEntity {
    
    /**
     * 팔로우를 하는 사용자 ID
     */
    @Column(name = "follower_id", nullable = false)
    private Long followerId;
    
    /**
     * 팔로우를 당하는 사용자 ID
     */
    @Column(name = "following_id", nullable = false)
    private Long followingId;
    
    /**
     * 팔로우 유형 (기본 팔로우)
     * 추후 확장 가능: FOLLOW, MUTE, BLOCK 등
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "follow_type", nullable = false, length = 20)
    private FollowType followType = FollowType.FOLLOW;
    
    /**
     * 알림 설정 여부
     * 이 사용자의 활동에 대한 알림을 받을지 여부
     */
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;
    
    @Builder
    public FollowJpaEntity(Long id, Long followerId, Long followingId, 
                          FollowType followType, Boolean notificationEnabled) {
        this.followerId = followerId;
        this.followingId = followingId;
        this.followType = followType != null ? followType : FollowType.FOLLOW;
        this.notificationEnabled = notificationEnabled != null ? notificationEnabled : true;
    }
    
    /**
     * 팔로우 유형 열거형
     */
    public enum FollowType {
        /**
         * 일반 팔로우
         */
        FOLLOW,
        
        /**
         * 뮤트 (팔로우하지만 타임라인에 표시 안함)
         */
        MUTE,
        
        /**
         * 차단
         */
        BLOCK
    }
    
    /**
     * 알림 설정 변경
     */
    public void toggleNotification() {
        this.notificationEnabled = !this.notificationEnabled;
    }
    
    /**
     * 팔로우 유형 변경
     */
    public void changeFollowType(FollowType newType) {
        this.followType = newType;
    }
    
    /**
     * 같은 사용자가 자기 자신을 팔로우하는지 검증
     */
    public boolean isSelfFollow() {
        return this.followerId.equals(this.followingId);
    }
    
    /**
     * 활성 팔로우 관계인지 확인 (삭제되지 않고 FOLLOW 타입)
     */
    public boolean isActiveFollow() {
        return !isDeleted() && followType == FollowType.FOLLOW;
    }
}
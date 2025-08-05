package org.userservice.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.userservice.infrastructure.common.BaseEntity;

/**
 * 사용자 JPA 엔티티
 * 도메인 모델과 데이터베이스 테이블 간의 순수한 매핑만 담당
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
           @UniqueConstraint(name = "uk_users_email", columnNames = "email")
       },
       indexes = {
           @Index(name = "idx_users_username", columnList = "username"),
           @Index(name = "idx_users_email", columnList = "email"),
           @Index(name = "idx_users_created_at", columnList = "createdAt")
       })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseEntity {
    
    /**
     * 사용자명 (고유)
     */
    @Column(nullable = false, length = 50)
    private String username;
    
    /**
     * 이메일 주소 (고유)
     */
    @Column(nullable = false, length = 100)
    private String email;
    
    /**
     * 암호화된 비밀번호
     */
    @Column(nullable = false, length = 255)
    private String password;
    
    /**
     * 프로필 자기소개
     */
    @Column(length = 160)
    private String profileBio;
    
    /**
     * 프로필 이미지 URL
     */
    @Column(length = 500)
    private String profileImageUrl;
    
    /**
     * 팔로워 수 (캐시용)
     */
    @Column(nullable = false)
    private Integer followerCount = 0;
    
    /**
     * 팔로잉 수 (캐시용)
     */
    @Column(nullable = false)
    private Integer followingCount = 0;
    
    /**
     * 트윗 수 (캐시용)
     */
    @Column(nullable = false)
    private Integer tweetCount = 0;
    
    /**
     * 이메일 인증 여부
     */
    @Column(nullable = false)
    private Boolean emailVerified = false;
    
    /**
     * 계정 활성화 여부
     */
    @Column(nullable = false)
    private Boolean active = true;
    
    /**
     * 계정 잠금 여부
     */
    @Column(nullable = false)
    private Boolean locked = false;
    
    @Builder
    public UserJpaEntity(Long id, String username, String email, String password,
                        String profileBio, String profileImageUrl,
                        Integer followerCount, Integer followingCount, Integer tweetCount,
                        Boolean emailVerified, Boolean active, Boolean locked) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileBio = profileBio;
        this.profileImageUrl = profileImageUrl;
        this.followerCount = followerCount != null ? followerCount : 0;
        this.followingCount = followingCount != null ? followingCount : 0;
        this.tweetCount = tweetCount != null ? tweetCount : 0;
        this.emailVerified = emailVerified != null ? emailVerified : false;
        this.active = active != null ? active : true;
        this.locked = locked != null ? locked : false;
    }
}

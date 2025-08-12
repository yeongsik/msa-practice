package org.userservice.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import org.userservice.domain.model.Follow;
import org.userservice.domain.model.FollowId;
import org.userservice.domain.model.UserId;
import org.userservice.infrastructure.persistence.entity.FollowJpaEntity;

/**
 * 팔로우 도메인 모델과 JPA 엔티티 간의 매핑을 담당하는 매퍼
 * DDD의 Infrastructure 계층에서 Domain 계층으로의 변환을 처리
 */
@Component
public class FollowMapper {
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     * 
     * @param entity JPA 엔티티
     * @return 도메인 모델
     */
    public Follow toDomain(FollowJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // JPA 엔티티의 필드를 도메인 모델 생성자에 매핑
        return new Follow(
                FollowId.of(entity.getId()),
                UserId.of(entity.getFollowerId()),
                UserId.of(entity.getFollowingId()),
                entity.getCreatedAt()
        );
    }
    
    /**
     * 도메인 모델을 JPA 엔티티로 변환
     * 
     * @param domain 도메인 모델
     * @return JPA 엔티티
     */
    public FollowJpaEntity toEntity(Follow domain) {
        if (domain == null) {
            return null;
        }
        
        // 도메인 모델의 필드를 JPA 엔티티 빌더에 매핑
        return FollowJpaEntity.builder()
                .id(domain.getId().value())
                .followerId(domain.getFollower().value())
                .followingId(domain.getFollowing().value())
                .followType(FollowJpaEntity.FollowType.FOLLOW) // 기본값
                .notificationEnabled(true) // 기본값
                .build();
    }
    
    /**
     * 도메인 모델을 기존 JPA 엔티티에 업데이트
     * 
     * @param domain 도메인 모델
     * @param entity 기존 JPA 엔티티
     * @return 업데이트된 JPA 엔티티
     */
    public FollowJpaEntity updateEntity(Follow domain, FollowJpaEntity entity) {
        if (domain == null || entity == null) {
            return entity;
        }
        
        // 도메인 모델의 상태를 JPA 엔티티에 반영
        if (!domain.isActive()) {
            entity.delete(); // BaseEntity의 소프트 삭제 메서드 호출
        } else if (domain.isActive() && entity.isDeleted()) {
            entity.restore(); // BaseEntity의 복구 메서드 호출
        }
        
        return entity;
    }
    
    /**
     * 새로운 팔로우 관계를 위한 JPA 엔티티 생성
     * ID는 자동 생성되므로 제외
     * 
     * @param followerId 팔로우하는 사용자 ID
     * @param followingId 팔로우받는 사용자 ID
     * @return 새로운 JPA 엔티티
     */
    public FollowJpaEntity createNewEntity(UserId followerId, UserId followingId) {
        return FollowJpaEntity.builder()
                .followerId(followerId.value())
                .followingId(followingId.value())
                .followType(FollowJpaEntity.FollowType.FOLLOW)
                .notificationEnabled(true)
                .build();
    }
}
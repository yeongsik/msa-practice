package org.userservice.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.userservice.domain.model.Follow;
import org.userservice.domain.model.UserId;
import org.userservice.domain.repository.FollowRepository;
import org.userservice.infrastructure.persistence.entity.FollowJpaEntity;
import org.userservice.infrastructure.persistence.mapper.FollowMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * FollowRepository 도메인 인터페이스의 Infrastructure 구현체
 * DDD의 Dependency Inversion Principle을 적용하여 도메인이 인프라에 의존하지 않도록 함
 */
@Repository
@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepository {
    
    private final FollowJpaRepository followJpaRepository;
    private final FollowMapper followMapper;
    
    @Override
    public Follow save(Follow follow) {
        FollowJpaEntity entity;
        
        if (follow.getId() != null) {
            // 기존 엔티티 업데이트
            Optional<FollowJpaEntity> existingEntity = followJpaRepository.findById(follow.getId().value());
            if (existingEntity.isPresent()) {
                entity = followMapper.updateEntity(follow, existingEntity.get());
            } else {
                entity = followMapper.toEntity(follow);
            }
        } else {
            // 새로운 엔티티 생성
            entity = followMapper.createNewEntity(follow.getFollower(), follow.getFollowing());
        }
        
        FollowJpaEntity savedEntity = followJpaRepository.save(entity);
        return followMapper.toDomain(savedEntity);
    }
    
    @Override
    public void delete(Follow follow) {
        if (follow.getId() != null) {
            followJpaRepository.findById(follow.getId().value())
                    .ifPresent(entity -> {
                        entity.delete(); // 소프트 삭제
                        followJpaRepository.save(entity);
                    });
        }
    }
    
    @Override
    public Optional<Follow> findByFollowerAndFollowing(UserId follower, UserId following) {
        return followJpaRepository.findByFollowerIdAndFollowingIdAndDeletedFalse(
                        follower.value(), following.value())
                .map(followMapper::toDomain);
    }
    
    @Override
    public boolean existsByFollowerAndFollowing(UserId follower, UserId following) {
        return followJpaRepository.existsActiveFollowByFollowerIdAndFollowingId(
                follower.value(), following.value());
    }
    
    @Override
    public List<Follow> findAllByFollower(UserId follower) {
        return followJpaRepository.findAllActiveFollowingByFollowerId(follower.value())
                .stream()
                .map(followMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Follow> findAllByFollowing(UserId following) {
        return followJpaRepository.findAllActiveFollowersByFollowingId(following.value())
                .stream()
                .map(followMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByFollower(UserId follower) {
        return followJpaRepository.countActiveFollowingByFollowerId(follower.value());
    }
    
    @Override
    public long countByFollowing(UserId following) {
        return followJpaRepository.countActiveFollowersByFollowingId(following.value());
    }
    
    /**
     * 상호 팔로우 관계 확인 (도메인 서비스에서 사용할 수 있는 추가 메서드)
     */
    public boolean existsMutualFollow(UserId user1, UserId user2) {
        return followJpaRepository.existsMutualFollow(user1.value(), user2.value());
    }
    
    /**
     * 팔로잉하는 사용자 ID 목록 조회 (성능 최적화용)
     */
    public List<Long> findFollowingUserIds(UserId follower) {
        return followJpaRepository.findFollowingUserIds(follower.value());
    }
    
    /**
     * 팔로워 사용자 ID 목록 조회 (성능 최적화용)
     */
    public List<Long> findFollowerUserIds(UserId following) {
        return followJpaRepository.findFollowerUserIds(following.value());
    }
}
package org.userservice.domain.repository;

import org.userservice.domain.model.Follow;
import org.userservice.domain.model.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 팔로우 관계 관리를 위한 리포지토리 인터페이스
 */
public interface FollowRepository {
    
    /**
     * 팔로우 관계 저장
     */
    Follow save(Follow follow);
    
    /**
     * 팔로우 관계 삭제
     */
    void delete(Follow follow);
    
    /**
     * 특정 팔로우 관계 조회
     */
    Optional<Follow> findByFollowerAndFollowing(UserId follower, UserId following);
    
    /**
     * 팔로우 관계 존재 여부 확인
     */
    boolean existsByFollowerAndFollowing(UserId follower, UserId following);
    
    /**
     * 사용자의 팔로잉 목록 조회
     */
    List<Follow> findAllByFollower(UserId follower);
    
    /**
     * 사용자의 팔로워 목록 조회
     */
    List<Follow> findAllByFollowing(UserId following);
    
    /**
     * 사용자의 팔로잉 수 조회
     */
    long countByFollower(UserId follower);
    
    /**
     * 사용자의 팔로워 수 조회
     */
    long countByFollowing(UserId following);
}
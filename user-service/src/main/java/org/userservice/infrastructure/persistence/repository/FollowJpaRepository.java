package org.userservice.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.userservice.infrastructure.persistence.entity.FollowJpaEntity;

import java.util.List;
import java.util.Optional;

/**
 * 팔로우 관계 Spring Data JPA 리포지토리
 * 데이터베이스 CRUD 작업을 담당
 */
@Repository
public interface FollowJpaRepository extends JpaRepository<FollowJpaEntity, Long> {
    
    /**
     * 특정 팔로우 관계 조회 (소프트 삭제 제외)
     */
    @Query("SELECT f FROM FollowJpaEntity f " +
           "WHERE f.followerId = :followerId " +
           "AND f.followingId = :followingId " +
           "AND f.deleted = false")
    Optional<FollowJpaEntity> findByFollowerIdAndFollowingIdAndDeletedFalse(
            @Param("followerId") Long followerId, 
            @Param("followingId") Long followingId);
    
    /**
     * 팔로우 관계 존재 여부 확인 (소프트 삭제 제외)
     */
    @Query("SELECT COUNT(f) > 0 FROM FollowJpaEntity f " +
           "WHERE f.followerId = :followerId " +
           "AND f.followingId = :followingId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW'")
    boolean existsActiveFollowByFollowerIdAndFollowingId(
            @Param("followerId") Long followerId, 
            @Param("followingId") Long followingId);
    
    /**
     * 사용자의 팔로잉 목록 조회 (활성 팔로우만)
     */
    @Query("SELECT f FROM FollowJpaEntity f " +
           "WHERE f.followerId = :followerId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW' " +
           "ORDER BY f.createdAt DESC")
    List<FollowJpaEntity> findAllActiveFollowingByFollowerId(@Param("followerId") Long followerId);
    
    /**
     * 사용자의 팔로워 목록 조회 (활성 팔로우만)
     */
    @Query("SELECT f FROM FollowJpaEntity f " +
           "WHERE f.followingId = :followingId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW' " +
           "ORDER BY f.createdAt DESC")
    List<FollowJpaEntity> findAllActiveFollowersByFollowingId(@Param("followingId") Long followingId);
    
    /**
     * 사용자의 팔로잉 수 조회 (활성 팔로우만)
     */
    @Query("SELECT COUNT(f) FROM FollowJpaEntity f " +
           "WHERE f.followerId = :followerId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW'")
    long countActiveFollowingByFollowerId(@Param("followerId") Long followerId);
    
    /**
     * 사용자의 팔로워 수 조회 (활성 팔로우만)
     */
    @Query("SELECT COUNT(f) FROM FollowJpaEntity f " +
           "WHERE f.followingId = :followingId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW'")
    long countActiveFollowersByFollowingId(@Param("followingId") Long followingId);
    
    /**
     * 상호 팔로우 관계 확인
     */
    @Query("SELECT COUNT(f) = 2 FROM FollowJpaEntity f " +
           "WHERE ((f.followerId = :userId1 AND f.followingId = :userId2) " +
           "OR (f.followerId = :userId2 AND f.followingId = :userId1)) " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW'")
    boolean existsMutualFollow(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    /**
     * 특정 사용자가 팔로우하는 사용자 ID 목록 (배치 처리용)
     */
    @Query("SELECT f.followingId FROM FollowJpaEntity f " +
           "WHERE f.followerId = :followerId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW'")
    List<Long> findFollowingUserIds(@Param("followerId") Long followerId);
    
    /**
     * 특정 사용자를 팔로우하는 사용자 ID 목록 (배치 처리용)
     */
    @Query("SELECT f.followerId FROM FollowJpaEntity f " +
           "WHERE f.followingId = :followingId " +
           "AND f.deleted = false " +
           "AND f.followType = 'FOLLOW'")
    List<Long> findFollowerUserIds(@Param("followingId") Long followingId);
}
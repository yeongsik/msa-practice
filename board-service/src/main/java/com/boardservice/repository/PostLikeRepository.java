package com.boardservice.repository;

import com.boardservice.entity.PostLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 게시글 좋아요 Repository.
 */
@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    /**
     * 중복 좋아요 확인.
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 좋아요 조회.
     */
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 게시글의 좋아요 수 조회.
     */
    long countByPostId(Long postId);

    /**
     * 사용자의 좋아요 목록 조회 (Post fetch join - N+1 방지).
     */
    @Query("SELECT pl FROM PostLike pl JOIN FETCH pl.post WHERE pl.userId = :userId ORDER BY pl.createdAt DESC")
    Page<PostLike> findByUserIdWithPost(@Param("userId") Long userId, Pageable pageable);

    /**
     * 사용자의 좋아요 수 조회.
     */
    long countByUserId(Long userId);
}

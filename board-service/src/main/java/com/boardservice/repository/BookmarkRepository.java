package com.boardservice.repository;

import com.boardservice.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 북마크 Repository.
 */
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 중복 북마크 확인.
     */
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    /**
     * 북마크 조회.
     */
    Optional<Bookmark> findByPostIdAndUserId(Long postId, Long userId);

    /**
     * 게시글의 북마크 수 조회.
     */
    long countByPostId(Long postId);

    /**
     * 사용자의 북마크 목록 조회 (Post fetch join - N+1 방지).
     */
    @Query("SELECT b FROM Bookmark b JOIN FETCH b.post WHERE b.userId = :userId ORDER BY b.createdAt DESC")
    Page<Bookmark> findByUserIdWithPost(@Param("userId") Long userId, Pageable pageable);

    /**
     * 사용자의 북마크 수 조회.
     */
    long countByUserId(Long userId);
}

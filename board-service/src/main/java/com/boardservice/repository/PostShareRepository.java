package com.boardservice.repository;

import com.boardservice.entity.PostShare;
import com.boardservice.entity.ShareType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 게시글 공유 Repository.
 */
@Repository
public interface PostShareRepository extends JpaRepository<PostShare, Long> {

    /**
     * 게시글의 공유 수 조회.
     */
    long countByPostId(Long postId);

    /**
     * 게시글의 공유 목록 조회 (최신순).
     */
    Page<PostShare> findByPostIdOrderByCreatedAtDesc(Long postId, Pageable pageable);

    /**
     * 사용자의 공유 목록 조회 (최신순).
     */
    Page<PostShare> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 공유 타입별 공유 수 조회.
     */
    long countByPostIdAndShareType(Long postId, ShareType shareType);

    /**
     * 게시글의 공유 타입별 통계.
     */
    List<PostShare> findByPostId(Long postId);
}

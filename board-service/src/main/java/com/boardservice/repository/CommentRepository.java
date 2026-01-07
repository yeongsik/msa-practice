package com.boardservice.repository;

import com.boardservice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 댓글 Repository.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 게시글의 댓글 목록 조회 (삭제되지 않은 것만, 생성일 오름차순).
     */
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    /**
     * 게시글의 댓글 수 조회 (삭제되지 않은 것만).
     */
    long countByPostIdAndIsDeletedFalse(Long postId);

    /**
     * 사용자의 댓글 목록 조회 (삭제되지 않은 것만, 최신순).
     */
    Page<Comment> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자의 댓글 수 조회 (삭제되지 않은 것만).
     */
    long countByUserIdAndIsDeletedFalse(Long userId);
}

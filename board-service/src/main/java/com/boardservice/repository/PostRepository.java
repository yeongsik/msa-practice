package com.boardservice.repository;

import com.boardservice.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시글 Repository.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 전체 게시글 조회 (최신순).
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 게시판별 게시글 조회 (최신순).
     */
    Page<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId, Pageable pageable);

    /**
     * 사용자별 게시글 조회 (최신순).
     */
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 게시글 상세 조회 (Board fetch join - N+1 방지).
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.board WHERE p.id = :id")
    Optional<Post> findByIdWithBoard(@Param("id") Long id);

    /**
     * 게시글 목록 조회 (Board fetch join - N+1 방지).
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.board ORDER BY p.createdAt DESC")
    List<Post> findAllWithBoard();

    /**
     * 게시판별 게시글 수 조회.
     */
    long countByBoardId(Long boardId);

    /**
     * 사용자별 게시글 수 조회.
     */
    long countByUserId(Long userId);

    /**
     * 검색 (제목 + 내용).
     */
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword% ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 인기 게시글 (좋아요 기준).
     */
    Page<Post> findAllByOrderByLikeCountDescCreatedAtDesc(Pageable pageable);

    /**
     * 인기 게시글 (조회수 기준).
     */
    Page<Post> findAllByOrderByViewCountDescCreatedAtDesc(Pageable pageable);
}

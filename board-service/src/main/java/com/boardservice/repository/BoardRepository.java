package com.boardservice.repository;

import com.boardservice.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    
    /**
     * 최신순으로 게시글 목록 조회
     */
    List<Board> findAllByOrderByCreatedAtDesc();

    /**
     * 특정 사용자가 작성한 게시글 조회
     */
    List<Board> findByUserId(Long userId);
}

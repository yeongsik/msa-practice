package com.boardservice.repository;

import com.boardservice.entity.BoardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 게시판 카테고리 Repository.
 */
@Repository
public interface BoardCategoryRepository extends JpaRepository<BoardCategory, Long> {

    /**
     * 이름으로 게시판 조회.
     */
    Optional<BoardCategory> findByName(String name);

    /**
     * 활성화된 게시판 목록 조회.
     */
    List<BoardCategory> findByIsActiveTrueOrderByCreatedAtAsc();

    /**
     * 게시판 이름 존재 여부 확인.
     */
    boolean existsByName(String name);
}

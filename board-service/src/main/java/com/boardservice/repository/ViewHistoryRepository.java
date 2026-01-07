package com.boardservice.repository;

import com.boardservice.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * 조회 이력 Repository.
 */
@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    /**
     * 오늘 해당 게시글을 이미 조회했는지 확인 (중복 방지).
     */
    boolean existsByPostIdAndUserIdAndViewDate(Long postId, Long userId, LocalDate viewDate);

    /**
     * 오래된 조회 이력 삭제 (배치 작업용).
     * @param cutoffDate 이 날짜보다 오래된 이력 삭제.
     */
    @Modifying
    @Query("DELETE FROM ViewHistory vh WHERE vh.viewDate < :cutoffDate")
    void deleteOldRecords(@Param("cutoffDate") LocalDate cutoffDate);

    /**
     * 게시글의 총 조회 수 조회.
     */
    long countByPostId(Long postId);

    /**
     * 게시글의 특정 기간 조회 수 조회.
     */
    @Query("SELECT COUNT(vh) FROM ViewHistory vh WHERE vh.postId = :postId AND vh.viewDate >= :startDate AND vh.viewDate <= :endDate")
    long countByPostIdAndDateBetween(@Param("postId") Long postId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

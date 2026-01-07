package com.boardservice.service;

import com.boardservice.entity.Post;
import com.boardservice.entity.ViewHistory;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.PostRepository;
import com.boardservice.repository.ViewHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 조회수 관리 서비스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewCountService {

    private final PostRepository postRepository;
    private final ViewHistoryRepository viewHistoryRepository;

    /**
     * 조회수 증가 (비동기 처리, 중복 방지).
     *
     * @param postId 게시글 ID
     * @param userId 사용자 ID (비로그인은 0)
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementViewCount(Long postId, Long userId) {
        LocalDate today = LocalDate.now();

        if (userId == null) {
            userId = 0L;
        }

        if (viewHistoryRepository.existsByPostIdAndUserIdAndViewDate(postId, userId, today)) {
            log.debug("이미 오늘 조회한 게시글: postId={}, userId={}", postId, userId);
            return;
        }

        ViewHistory history = ViewHistory.builder()
                .postId(postId)
                .userId(userId)
                .viewDate(today)
                .createdAt(LocalDateTime.now())
                .build();

        viewHistoryRepository.save(history);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("게시글을 찾을 수 없습니다. id=" + postId));

        post.incrementViewCount();

        log.debug("조회수 증가: postId={}, userId={}, newCount={}",
                postId, userId, post.getViewCount());
    }

    /**
     * 오래된 조회 이력 삭제 (배치 작업 - 매일 새벽 3시).
     * 30일 이상된 이력 삭제.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanOldViewHistory() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);

        try {
            viewHistoryRepository.deleteOldRecords(cutoffDate);
            log.info("오래된 조회 이력 삭제 완료: cutoffDate={}", cutoffDate);
        } catch (Exception e) {
            log.error("조회 이력 삭제 실패", e);
        }
    }

    /**
     * 게시글의 일자별 조회수 조회.
     */
    @Transactional(readOnly = true)
    public long getViewCountByDateRange(Long postId, LocalDate startDate, LocalDate endDate) {
        return viewHistoryRepository.countByPostIdAndDateBetween(postId, startDate, endDate);
    }

    /**
     * 게시글의 총 조회 이력 수.
     */
    @Transactional(readOnly = true)
    public long getTotalViewHistoryCount(Long postId) {
        return viewHistoryRepository.countByPostId(postId);
    }
}

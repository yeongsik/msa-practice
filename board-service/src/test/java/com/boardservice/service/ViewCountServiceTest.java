package com.boardservice.service;

import java.time.LocalDate;
import java.util.Optional;

import com.boardservice.entity.BoardCategory;
import com.boardservice.entity.Post;
import com.boardservice.entity.ViewHistory;
import com.boardservice.exception.PostNotFoundException;
import com.boardservice.repository.PostRepository;
import com.boardservice.repository.ViewHistoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * ViewCountService 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
class ViewCountServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ViewHistoryRepository viewHistoryRepository;

    @InjectMocks
    private ViewCountService viewCountService;

    private Post post;

    @BeforeEach
    void setUp() {
        BoardCategory boardCategory = BoardCategory.builder()
                .id(1L)
                .name("자유게시판")
                .description("자유롭게 소통하는 공간")
                .isActive(true)
                .postCount(0)
                .build();

        post = Post.builder()
                .id(1L)
                .title("테스트 게시글")
                .content("테스트 내용")
                .userId(1L)
                .board(boardCategory)
                .build();
    }

    @Test
    @DisplayName("조회수 증가 성공 - 첫 조회")
    void incrementViewCount_Success_FirstView() {
        // given
        LocalDate today = LocalDate.now();
        given(viewHistoryRepository.existsByPostIdAndUserIdAndViewDate(1L, 1L, today))
                .willReturn(false);
        given(viewHistoryRepository.save(any(ViewHistory.class)))
                .willReturn(ViewHistory.builder()
                        .postId(1L)
                        .userId(1L)
                        .viewDate(today)
                        .build());
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        long initialViewCount = post.getViewCount();

        // when
        viewCountService.incrementViewCount(1L, 1L);

        // then
        verify(viewHistoryRepository).existsByPostIdAndUserIdAndViewDate(1L, 1L, today);
        verify(viewHistoryRepository).save(any(ViewHistory.class));
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("조회수 증가 무시 - 오늘 이미 조회함")
    void incrementViewCount_Ignore_AlreadyViewedToday() {
        // given
        LocalDate today = LocalDate.now();
        given(viewHistoryRepository.existsByPostIdAndUserIdAndViewDate(1L, 1L, today))
                .willReturn(true);

        // when
        viewCountService.incrementViewCount(1L, 1L);

        // then
        verify(viewHistoryRepository).existsByPostIdAndUserIdAndViewDate(1L, 1L, today);
        verify(viewHistoryRepository, never()).save(any(ViewHistory.class));
        verify(postRepository, never()).findById(any());
    }

    @Test
    @DisplayName("조회수 증가 - 비로그인 사용자 (userId = null)")
    void incrementViewCount_Success_GuestUser() {
        // given
        LocalDate today = LocalDate.now();
        given(viewHistoryRepository.existsByPostIdAndUserIdAndViewDate(1L, 0L, today))
                .willReturn(false);
        given(viewHistoryRepository.save(any(ViewHistory.class)))
                .willReturn(ViewHistory.builder()
                        .postId(1L)
                        .userId(0L)
                        .viewDate(today)
                        .build());
        given(postRepository.findById(1L)).willReturn(Optional.of(post));

        // when
        viewCountService.incrementViewCount(1L, null);

        // then
        verify(viewHistoryRepository).existsByPostIdAndUserIdAndViewDate(1L, 0L, today);
        verify(viewHistoryRepository).save(any(ViewHistory.class));
        verify(postRepository).findById(1L);
    }

    @Test
    @DisplayName("일자별 조회수 조회")
    void getViewCountByDateRange_Success() {
        // given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        given(viewHistoryRepository.countByPostIdAndDateBetween(1L, startDate, endDate))
                .willReturn(10L);

        // when
        long count = viewCountService.getViewCountByDateRange(1L, startDate, endDate);

        // then
        assertThat(count).isEqualTo(10L);

        verify(viewHistoryRepository).countByPostIdAndDateBetween(1L, startDate, endDate);
    }

    @Test
    @DisplayName("게시글의 총 조회 이력 수 조회")
    void getTotalViewHistoryCount_Success() {
        // given
        given(viewHistoryRepository.countByPostId(1L)).willReturn(100L);

        // when
        long count = viewCountService.getTotalViewHistoryCount(1L);

        // then
        assertThat(count).isEqualTo(100L);

        verify(viewHistoryRepository).countByPostId(1L);
    }
}

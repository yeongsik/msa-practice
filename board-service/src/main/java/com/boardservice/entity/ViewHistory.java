package com.boardservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 조회 이력 엔티티 (조회수 중복 카운트 방지용)
 */
@Entity
@Table(name = "view_histories", indexes = {
        @Index(name = "idx_post_user_date", columnList = "post_id, user_id, view_date")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글 ID
     */
    @Column(nullable = false)
    private Long postId;

    /**
     * 조회한 사용자 ID (비로그인 사용자는 0 또는 null)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 조회 날짜 (일자별 1회만 카운트)
     */
    @Column(nullable = false)
    private LocalDate viewDate;

    /**
     * IP 주소 (선택사항 - 추가 중복 방지용)
     */
    @Column(length = 50)
    private String ipAddress;

    /**
     * 생성 시간
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (viewDate == null) {
            viewDate = LocalDate.now();
        }
    }
}

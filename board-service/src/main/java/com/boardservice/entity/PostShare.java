package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시글 공유 엔티티
 */
@Entity
@Table(name = "post_shares", indexes = {
        @Index(name = "idx_post_created", columnList = "post_id, created_at"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostShare extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 공유한 사용자 ID
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 공유 타입 (링크, 카카오톡, 트위터 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ShareType shareType;
}

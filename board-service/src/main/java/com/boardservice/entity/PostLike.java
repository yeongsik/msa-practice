package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 게시글 좋아요 엔티티
 */
@Entity
@Table(name = "post_likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_post_user",
                columnNames = {"post_id", "user_id"}
        ),
        indexes = @Index(name = "idx_user_id", columnList = "user_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostLike extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "snowflake-id")
    @GenericGenerator(name = "snowflake-id", strategy = "com.common.util.id.SnowflakeIdentifierGenerator")
    private Long id;

    /**
     * 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 좋아요한 사용자 ID
     */
    @Column(nullable = false)
    private Long userId;
}

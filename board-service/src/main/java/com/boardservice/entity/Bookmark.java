package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 게시글 북마크(즐겨찾기) 엔티티
 */
@Entity
@Table(name = "bookmarks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_bookmark_post_user",
                columnNames = {"post_id", "user_id"}
        ),
        indexes = @Index(name = "idx_user_created", columnList = "user_id, created_at")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Bookmark extends BaseTimeEntity {

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
     * 북마크한 사용자 ID
     */
    @Column(nullable = false)
    private Long userId;
}

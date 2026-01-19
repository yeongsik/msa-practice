package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

/**
 * 댓글 엔티티
 */
@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_post_created", columnList = "post_id, created_at"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "snowflake-id")
    @GenericGenerator(name = "snowflake-id", strategy = "com.common.util.id.SnowflakeIdentifierGenerator")
    private Long id;

    /**
     * 댓글 내용
     */
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 작성자 ID (User Service의 User ID)
     */
    @Column(nullable = false)
    private Long userId;

    /**
     * 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 소프트 삭제 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    /**
     * 댓글 수정
     */
    public void update(String content) {
        this.content = content;
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    public void delete() {
        this.isDeleted = true;
        this.content = "삭제된 댓글입니다.";
    }
}

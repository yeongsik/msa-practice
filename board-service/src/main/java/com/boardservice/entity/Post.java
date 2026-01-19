package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 엔티티 (기존 Board의 역할을 대체)
 */
@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_board_created", columnList = "board_id, created_at"),
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "snowflake-id")
    @GenericGenerator(name = "snowflake-id", strategy = "com.common.util.id.SnowflakeIdentifierGenerator")
    private Long id;

    /**
     * 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 내용
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
     * 게시판 카테고리
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardCategory board;

    /**
     * 조회수
     */
    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    /**
     * 좋아요 수 (비정규화)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    /**
     * 댓글 수 (비정규화)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    /**
     * 북마크 수 (비정규화)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer bookmarkCount = 0;

    /**
     * 공유 수
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer shareCount = 0;

    /**
     * 댓글 목록
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    /**
     * 좋아요 목록
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    /**
     * 북마크 목록
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    /**
     * 공유 목록
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostShare> shares = new ArrayList<>();

    /**
     * 게시글 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * 좋아요 수 감소
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 댓글 수 증가
     */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 수 감소
     */
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    /**
     * 북마크 수 증가
     */
    public void incrementBookmarkCount() {
        this.bookmarkCount++;
    }

    /**
     * 북마크 수 감소
     */
    public void decrementBookmarkCount() {
        if (this.bookmarkCount > 0) {
            this.bookmarkCount--;
        }
    }

    /**
     * 공유 수 증가
     */
    public void incrementShareCount() {
        this.shareCount++;
    }
}

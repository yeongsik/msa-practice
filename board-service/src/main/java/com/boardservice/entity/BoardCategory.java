package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시판 카테고리 엔티티
 */
@Entity
@Table(name = "board_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BoardCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "snowflake-id")
    @GenericGenerator(name = "snowflake-id", strategy = "com.common.util.id.SnowflakeIdentifierGenerator")
    private Long id;

    /**
     * 게시판 이름 (예: "공지사항", "자유게시판", "질문게시판")
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * 게시판 설명
     */
    @Column(length = 500)
    private String description;

    /**
     * 활성화 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * 게시글 수 (비정규화 - 성능 최적화)
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer postCount = 0;

    /**
     * 게시판의 게시글 목록
     */
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    /**
     * 게시판 정보 수정
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * 게시판 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 게시판 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 게시글 수 증가
     */
    public void incrementPostCount() {
        this.postCount++;
    }

    /**
     * 게시글 수 감소
     */
    public void decrementPostCount() {
        if (this.postCount > 0) {
            this.postCount--;
        }
    }
}

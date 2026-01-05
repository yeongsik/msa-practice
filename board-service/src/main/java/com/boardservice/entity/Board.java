package com.boardservice.entity;

import com.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 게시글 엔티티
 */
@Entity
@Table(name = "boards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
     * 게시글 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

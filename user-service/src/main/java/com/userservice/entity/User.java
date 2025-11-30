package com.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 엔티티
 * 사용자 정보를 저장하는 테이블과 매핑
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    /**
     * 사용자 ID (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자명 (고유값)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 비밀번호 (BCrypt 암호화)
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 이메일 (고유값)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 생성일시
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 비밀번호 업데이트 (비즈니스 로직)
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 이메일 업데이트 (비즈니스 로직)
     */
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }
}

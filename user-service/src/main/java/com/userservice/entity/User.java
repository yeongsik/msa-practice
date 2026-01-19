package com.userservice.entity;

import com.common.entity.BaseTimeEntity;
import com.common.type.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

/**
 * 사용자 엔티티.
 * 사용자 정보를 저장하는 테이블과 매핑.
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    /**
     * 사용자 ID (Primary Key).
     */
    @Id
    @GeneratedValue(generator = "snowflake-id")
    @GenericGenerator(name = "snowflake-id", strategy = "com.common.util.id.SnowflakeIdentifierGenerator")
    private Long id;

    /**
     * 사용자명 (고유값).
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * 비밀번호 (BCrypt 암호화).
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * 이메일 (고유값).
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * 사용자 권한.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * 비밀번호 업데이트 (비즈니스 로직).
     *
     * @param newPassword 새로운 비밀번호
     */
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    /**
     * 이메일 업데이트 (비즈니스 로직).
     *
     * @param newEmail 새로운 이메일
     */
    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }
}
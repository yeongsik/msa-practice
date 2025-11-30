package com.userservice.repository;

import com.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티에 대한 데이터 접근 인터페이스
 * JpaRepository를 상속받아 기본 CRUD 기능 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명으로 사용자 조회
     *
     * @param username 사용자명
     * @return Optional<User>
     */
    Optional<User> findByUsername(String username);

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return Optional<User>
     */
    Optional<User> findByEmail(String email);

    /**
     * 사용자명 존재 여부 확인
     *
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);

    /**
     * 이메일 존재 여부 확인
     *
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
}
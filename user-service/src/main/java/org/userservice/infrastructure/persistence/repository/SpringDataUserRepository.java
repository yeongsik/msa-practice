package org.userservice.infrastructure.persistence.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.userservice.infrastructure.persistence.entity.UserJpaEntity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<UserJpaEntity, Long> {

    Optional<UserJpaEntity> findByUsernameAndDeletedFalse(String username);
    Optional<UserJpaEntity> findByEmailAndDeletedFalse(String email);
    boolean existsByUsernameAndDeletedFalse(String username);
    boolean existsByEmailAndDeletedFalse(String email);
    Page<UserJpaEntity> findByDeletedFalse(Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.createdAt BETWEEN :from AND :to")
    long countByCreatedAtBetween(@Param("from") LocalDateTime from,
                                 @Param("to") LocalDateTime to);


}

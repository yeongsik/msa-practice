package org.userservice.domain.repository;

import org.userservice.domain.model.Email;
import org.userservice.domain.model.User;
import org.userservice.domain.model.UserId;
import org.userservice.domain.model.Username;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // 도메인 값 객체 사용
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);

    // 도메인 엔티티 사용
    User save(User user);
    void delete(UserId userId);

    // 도메인 의미있는 메서드명
    boolean existsActiveUserByUsername(Username username);
    boolean existsActiveUserByEmail(Email email);

    // 페이징은 도메인 개념으로
    List<User> findRecentUsers(int limit);
    List<User> findUsersByCreatedDateRange(LocalDateTime from, LocalDateTime to);
}

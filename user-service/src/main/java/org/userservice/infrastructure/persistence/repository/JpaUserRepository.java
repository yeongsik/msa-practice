package org.userservice.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.userservice.domain.model.Email;
import org.userservice.domain.model.User;
import org.userservice.domain.model.UserId;
import org.userservice.domain.model.Username;
import org.userservice.domain.repository.UserRepository;
import org.userservice.infrastructure.persistence.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserMapper userMapper;

    public JpaUserRepository(SpringDataUserRepository springDataUserRepository, UserMapper userMapper) {
        this.springDataUserRepository = springDataUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public void delete(UserId userId) {

    }

    @Override
    public boolean existsActiveUserByUsername(Username username) {
        return false;
    }

    @Override
    public boolean existsActiveUserByEmail(Email email) {
        return false;
    }

    @Override
    public List<User> findRecentUsers(int limit) {
        return List.of();
    }

    @Override
    public List<User> findUsersByCreatedDateRange(LocalDateTime from, LocalDateTime to) {
        return List.of();
    }
}

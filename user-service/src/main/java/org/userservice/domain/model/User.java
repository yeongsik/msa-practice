package org.userservice.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;

public class User {

    private final UserId id;
    @Getter
    private final Username username;
    @Getter
    private final Email email;
    @Getter
    private Password password;
    @Getter
    private Profile profile;

    @Getter
    private final LocalDateTime createdAt;
    @Getter
    private LocalDateTime updatedAt;
    @Getter
    private boolean deleted;

    public User(UserId id, Username username, Email email, Password password, Profile profile, LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }

    public void updateProfile(Profile profile) {
        this.profile = profile;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(Password currentPassword, Password newPassword) {
        if (!this.password.matches(currentPassword)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeFollowedBy(User follower) {
        return !this.equals(follower) && !this.deleted && !follower.deleted;
    }

    public UserId getUserId() {
        return id;
    }

    public void delete() {
        this.deleted = true;
        this.updatedAt = LocalDateTime.now();
    }

}

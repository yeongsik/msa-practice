package org.userservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.userservice.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "Username must be 3-20 characters and contain only letters, numbers, and underscores")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column(length = 160)
    private String bio;

    private String profileImage;

    @Builder
    public User(String username, String email, String password, String bio, String profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.profileImage = profileImage;
    }

    public void updateProfile(String bio, String profileImage) {
        this.bio = bio;
        this.profileImage = profileImage;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public boolean isValidUsername() {
        return username != null && 
               username.length() >= 3 && 
               username.length() <= 20 &&
               username.matches("^[a-zA-Z0-9_]+$");
    }

    public boolean isValidBio() {
        return bio == null || bio.length() <= 160;
    }
}

package org.userservice.application.dto;

import org.userservice.domain.User;

import java.time.LocalDateTime;

/**
 * User response DTO
 */
public record UserResponse(
    Long id,
    String username,
    String email,
    String bio,
    String profileImage,
    LocalDateTime createdAt
) {
    
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getBio(),
            user.getProfileImage(),
            user.getCreatedAt()
        );
    }
    
    public static UserResponse fromWithoutEmail(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            null, // Hide email for public view
            user.getBio(),
            user.getProfileImage(),
            user.getCreatedAt()
        );
    }
}
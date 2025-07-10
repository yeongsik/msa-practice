package org.userservice.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * User registration request DTO
 */
public record UserCreateRequest(
    @NotBlank(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", 
             message = "Username must be 3-20 characters and contain only letters, numbers, and underscores")
    String username,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    String email,
    
    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Password must be at least 8 characters with letters, numbers, and special characters")
    String password,
    
    @Size(max = 160, message = "Bio must be 160 characters or less")
    String bio,
    
    String profileImage
) {}
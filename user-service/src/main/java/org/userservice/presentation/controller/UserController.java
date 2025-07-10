package org.userservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.userservice.application.dto.UserCreateRequest;
import org.userservice.application.dto.UserResponse;
import org.userservice.application.usecase.UserService;
import org.userservice.common.ApiResponse;

/**
 * User REST API Controller
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * User registration endpoint
     * 
     * @param request user creation request
     * @return created user response
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("User registration request received for username: {}", request.username());
        
        UserResponse response = userService.createUser(request);
        
        log.info("User registration completed for username: {}", request.username());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    /**
     * Get user by ID
     * 
     * @param userId user ID
     * @return user response
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        log.info("Get user request received for ID: {}", userId);
        
        UserResponse response = userService.findById(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get user by username
     * 
     * @param username username
     * @return user response
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        log.info("Get user request received for username: {}", username);
        
        UserResponse response = userService.findByUsername(username);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Check if username exists
     * 
     * @param username username to check
     * @return availability response
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<ApiResponse<Boolean>> checkUsernameAvailability(@PathVariable String username) {
        log.info("Username availability check for: {}", username);
        
        boolean exists = userService.existsByUsername(username);
        boolean available = !exists;
        
        return ResponseEntity.ok(ApiResponse.success(available));
    }

    /**
     * Check if email exists
     * 
     * @param email email to check
     * @return availability response
     */
    @GetMapping("/check/email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(@RequestParam String email) {
        log.info("Email availability check for: {}", email);
        
        boolean exists = userService.existsByEmail(email);
        boolean available = !exists;
        
        return ResponseEntity.ok(ApiResponse.success(available));
    }
}
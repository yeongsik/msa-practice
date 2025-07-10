package org.userservice.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.application.dto.UserCreateRequest;
import org.userservice.application.dto.UserResponse;
import org.userservice.common.exception.BusinessException;
import org.userservice.common.exception.ErrorCode;
import org.userservice.domain.User;
import org.userservice.domain.UserRepository;

/**
 * User business logic service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Creates a new user
     * 
     * @param request user creation request
     * @return created user response
     * @throws BusinessException when username or email already exists
     */
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Creating new user with username: {}", request.username());
        
        // Validate username uniqueness
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Username already exists: {}", request.username());
            throw new BusinessException(ErrorCode.USER_001);
        }
        
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email already exists: {}", request.email());
            throw new BusinessException(ErrorCode.USER_002);
        }
        
        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(request.password());
        
        // Set default profile image if not provided
        String profileImage = request.profileImage() != null ? 
            request.profileImage() : getDefaultProfileImage();
        
        // Create user entity
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(encryptedPassword)
                .bio(request.bio())
                .profileImage(profileImage)
                .build();
        
        // Save user
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        // TODO: Publish UserCreatedEvent to Kafka
        // kafkaTemplate.send("user-events", new UserCreatedEvent(savedUser.getId(), savedUser.getUsername()));
        
        return UserResponse.from(savedUser);
    }
    
    /**
     * Finds user by ID
     * 
     * @param userId user ID
     * @return user response
     * @throws BusinessException when user not found
     */
    public UserResponse findById(Long userId) {
        User user = userRepository.findByIdAndNotDeleted(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_003));
        
        return UserResponse.from(user);
    }
    
    /**
     * Finds user by username
     * 
     * @param username username
     * @return user response
     * @throws BusinessException when user not found
     */
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_003));
        
        return UserResponse.from(user);
    }
    
    /**
     * Checks if username exists
     * 
     * @param username username to check
     * @return true if exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Checks if email exists
     * 
     * @param email email to check
     * @return true if exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    private String getDefaultProfileImage() {
        return "https://default-profile-image.com/default.png";
    }
}
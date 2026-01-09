package com.boardservice.client;

import com.common.dto.ApiResponse;
import com.common.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserServiceClientFallbackFactory implements FallbackFactory<UserServiceClient> {

    @Override
    public UserServiceClient create(Throwable cause) {
        return new UserServiceClient() {
            @Override
            public ApiResponse<UserResponse> getUser(Long id) {
                log.error("User Service getUser failed for id: {}. Cause: {}", id, cause.getMessage());
                // Fallback: Return "Unknown User"
                UserResponse fallbackUser = UserResponse.builder()
                        .id(id)
                        .username("Unknown User")
                        .email("")
                        .build();
                return ApiResponse.success(fallbackUser);
            }

            @Override
            public ApiResponse<UserDto> validateToken(String token) {
                log.error("User Service validateToken failed. Cause: {}", cause.getMessage());
                // Fallback: Fail authentication safely
                return ApiResponse.error("User Service Unavailable for Token Validation");
            }
        };
    }
}

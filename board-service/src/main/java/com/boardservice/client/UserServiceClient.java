package com.boardservice.client;

import com.common.dto.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    ApiResponse<UserResponse> getUser(@PathVariable("id") Long id);

    @Getter
    @NoArgsConstructor
    class UserResponse {
        private Long id;
        private String username;
        private String email;
    }
}

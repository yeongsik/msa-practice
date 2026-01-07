package com.boardservice.client;

import com.common.dto.ApiResponse;
import com.common.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", url = "http://localhost:8080")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    ApiResponse<UserResponse> getUser(@PathVariable("id") Long id);

    @GetMapping("/api/users/validate")
    ApiResponse<UserDto> validateToken(@RequestHeader("Authorization") String token);

    @Getter
    @NoArgsConstructor
    class UserResponse {
        private Long id;
        private String username;
        private String email;
    }
}

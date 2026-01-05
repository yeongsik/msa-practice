package com.userservice.controller;

import com.common.dto.ApiResponse;
import com.userservice.dto.LoginRequest;
import com.userservice.dto.LoginResponse;
import com.userservice.dto.MyInfoResponse;
import com.userservice.dto.SignUpRequest;
import com.userservice.dto.UpdateUserRequest;
import com.userservice.dto.UpdateUserResponse;
import com.userservice.dto.UserResponse;
import com.userservice.security.CustomUserDetails;
import com.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 컨트롤러
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     *
     * @param request 회원가입 요청
     * @return ApiResponse<UserResponse> (201 Created)
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("POST /api/users/signup - 회원가입 요청");
        UserResponse response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 로그인 API
     *
     * @param request 로그인 요청
     * @return ApiResponse<LoginResponse> (200 OK)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/users/login - 로그인 요청: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 정보 조회 API
     * 헤더의 JWT 토큰을 통해 사용자 인증을 수행합니다.
     *
     * @param userDetails 인증된 사용자 정보 (@AuthenticationPrincipal)
     * @return ApiResponse<MyInfoResponse>
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("GET /api/users/me - 내 정보 조회 요청: userId={}", userDetails.getId());
        MyInfoResponse response = userService.getMyInfo(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자 정보 수정 API
     *
     * @param id          사용자 ID (Path Variable)
     * @param request     수정 요청 정보
     * @param userDetails 인증된 사용자 정보
     * @return ApiResponse<UpdateUserResponse>
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        log.info("PUT /api/users/{} - 정보 수정 요청", id);

        // 본인 확인
        if (!userDetails.getId().equals(id)) {
            log.warn("권한 없음: 본인의 정보만 수정할 수 있습니다. 요청자={}, 대상={}", userDetails.getId(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("본인의 정보만 수정할 수 있습니다."));
        }

        UpdateUserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
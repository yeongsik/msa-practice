package com.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common.dto.ApiResponse;
import com.userservice.dto.LoginRequest;
import com.userservice.dto.LoginResponse;
import com.userservice.dto.MyInfoResponse;
import com.userservice.dto.SignUpRequest;
import com.userservice.dto.TokenReissueRequest;
import com.userservice.dto.UpdateUserRequest;
import com.userservice.dto.UpdateUserResponse;
import com.userservice.dto.UserResponse;
import com.userservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 컨트롤러.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API.
     *
     * @param request 회원가입 요청
     * @return ApiResponse{@code <UserResponse>} (201 Created)
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        log.info("POST /api/users/signup - 회원가입 요청");
        UserResponse response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 로그인 API.
     *
     * @param request 로그인 요청
     * @return ApiResponse{@code <LoginResponse>} (200 OK)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/users/login - 로그인 요청: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 정보 조회 API.
     * 헤더의 JWT 토큰을 통해 사용자 인증을 수행합니다.
     *
     * @param userId 인증된 사용자 ID (@AuthenticationPrincipal)
     * @return ApiResponse{@code <MyInfoResponse>}
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMyInfo(@AuthenticationPrincipal Long userId) {
        log.info("GET /api/users/me - 내 정보 조회 요청: userId={}", userId);
        MyInfoResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자 정보 조회 API (내부 통신용).
     *
     * @param id 사용자 ID
     * @return ApiResponse{@code <UserResponse>}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        log.info("GET /api/users/{} - 사용자 정보 조회 요청", id);
        UserResponse response = userService.getUser(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 사용자 정보 수정 API.
     *
     * @param id          사용자 ID (Path Variable)
     * @param request     수정 요청 정보
     * @param userId      인증된 사용자 ID
     * @return ApiResponse{@code <UpdateUserResponse>}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("PUT /api/users/{} - 정보 수정 요청", id);

        // 본인 확인
        if (!userId.equals(id)) {
            log.warn("권한 없음: 본인의 정보만 수정할 수 있습니다. 요청자={}, 대상={}", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("본인의 정보만 수정할 수 있습니다."));
        }

        UpdateUserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 검증 API (다른 서비스 호출용).
     *
     * @param authHeader Authorization 헤더
     * @return UserDto (검증된 사용자 정보)
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<com.common.dto.UserDto>> validateToken(
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("GET /api/users/validate - 토큰 검증 요청");
        String token = authHeader;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        com.common.dto.UserDto response = userService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 토큰 재발급 API.
     * Refresh Token을 이용하여 새로운 Access Token을 발급합니다.
     *
     * @param request Refresh Token
     * @return ApiResponse{@code <LoginResponse>}
     */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(@Valid @RequestBody TokenReissueRequest request) {
        log.info("POST /api/users/reissue - 토큰 재발급 요청");
        LoginResponse response = userService.reissue(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 로그아웃 API.
     * DB에서 Refresh Token을 삭제하고 Access Token을 블랙리스트에 추가합니다.
     *
     * @param userId     인증된 사용자 ID
     * @param authHeader Authorization 헤더
     * @return ApiResponse{@code <Void>}
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal Long userId,
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("POST /api/users/logout - 로그아웃 요청: userId={}", userId);
        String accessToken = authHeader.substring(7);
        userService.logout(userId, accessToken);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

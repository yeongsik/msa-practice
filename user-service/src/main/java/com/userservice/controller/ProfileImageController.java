package com.userservice.controller;

import com.common.dto.ApiResponse;
import com.userservice.dto.ProfileImageResponse;
import com.userservice.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class ProfileImageController {

    private final ProfileImageService profileImageService;

    /**
     * 프로필 이미지 업로드/수정 API.
     *
     * @param id     사용자 ID
     * @param userId 인증된 사용자 ID
     * @param file   이미지 파일
     * @return ProfileImageResponse
     */
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<ApiResponse<ProfileImageResponse>> uploadProfileImage(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId,
            @RequestParam("profileImage") MultipartFile file
    ) {
        log.info("POST /api/users/{}/profile-image", id);

        if (!userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("본인의 프로필 이미지만 수정할 수 있습니다."));
        }

        ProfileImageResponse response = profileImageService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 프로필 이미지 삭제 API.
     *
     * @param id     사용자 ID
     * @param userId 인증된 사용자 ID
     * @return Void
     */
    @DeleteMapping("/{id}/profile-image")
    public ResponseEntity<ApiResponse<Void>> deleteProfileImage(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId
    ) {
        log.info("DELETE /api/users/{}/profile-image", id);

        if (!userId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("본인의 프로필 이미지만 삭제할 수 있습니다."));
        }

        profileImageService.deleteProfileImage(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}

package com.userservice.service;

import com.userservice.dto.ProfileImageResponse;
import com.userservice.entity.User;
import com.userservice.exception.ImageUploadException;
import com.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {

    private final UserRepository userRepository;

    @Value("${app.upload.base-path}")
    private String uploadBasePath;

    @Value("${app.upload.max-file-size}")
    private long maxFileSize;

    @Value("${app.upload.allowed-types}")
    private List<String> allowedTypes;

    @Value("${app.upload.thumbnail-size}")
    private int thumbnailSize;

    @Value("${app.upload.profile-size}")
    private int profileSize;

    private final Tika tika = new Tika();

    @Transactional
    public ProfileImageResponse uploadProfileImage(Long userId, MultipartFile file) {
        log.info("프로필 이미지 업로드 요청: userId={}, fileName={}", userId, file.getOriginalFilename());

        // 1. 사용자 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 파일 검증
        validateFile(file);

        // 3. 저장 경로 생성 (연/월/일)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path directoryPath = Paths.get(uploadBasePath, datePath).toAbsolutePath();
        
        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new ImageUploadException("업로드 디렉토리 생성 실패", e);
        }

        // 4. 파일명 생성
        String uuid = UUID.randomUUID().toString();
        String extension = getExtension(file.getOriginalFilename());
        
        String profileFileName = uuid + "_profile." + extension;
        String thumbnailFileName = uuid + "_thumbnail." + extension;

        File profileFile = new File(directoryPath.toFile(), profileFileName);
        File thumbnailFile = new File(directoryPath.toFile(), thumbnailFileName);

        try {
            // 5. 이미지 처리 및 저장
            // 원본 리사이징 (Profile Version)
            Thumbnails.of(file.getInputStream())
                    .size(profileSize, profileSize)
                    .outputQuality(0.85)
                    .toFile(profileFile);

            // 썸네일 생성
            Thumbnails.of(file.getInputStream())
                    .size(thumbnailSize, thumbnailSize)
                    .outputQuality(0.75)
                    .toFile(thumbnailFile);

        } catch (IOException e) {
            throw new ImageUploadException("이미지 처리 실패", e);
        }

        // 6. 기존 이미지 삭제 (있다면)
        deleteOldImages(user);

        // 7. URL 생성 (상대 경로)
        // 예: /uploads/2026/01/12/uuid_profile.jpg
        // WebMvcConfig에서 /uploads/** 요청을 file:///{uploadBasePath}/ 매핑해야 함.
        String profileUrl = "/uploads/" + datePath + "/" + profileFileName;
        String thumbnailUrl = "/uploads/" + datePath + "/" + thumbnailFileName;

        // 8. DB 업데이트
        user.updateProfileImage(profileUrl, thumbnailUrl);

        return ProfileImageResponse.of(userId, profileUrl, thumbnailUrl);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        deleteOldImages(user);
        user.updateProfileImage(null, null);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ImageUploadException("파일이 비어있습니다.");
        }

        if (file.getSize() > maxFileSize) {
            throw new ImageUploadException("파일 크기가 제한을 초과했습니다. (최대 " + (maxFileSize / 1024 / 1024) + "MB)");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!allowedTypes.contains(extension.toLowerCase()) && !allowedTypes.contains(extension.toUpperCase())) {
            throw new ImageUploadException("지원하지 않는 파일 형식입니다. (지원: " + allowedTypes + ")");
        }

        try {
            String mimeType = tika.detect(file.getInputStream());
            if (!mimeType.startsWith("image/")) {
                throw new ImageUploadException("이미지 파일만 업로드 가능합니다.");
            }
        } catch (IOException e) {
            throw new ImageUploadException("파일 검사 중 오류가 발생했습니다.", e);
        }
    }

    private void deleteOldImages(User user) {
        // 기존 URL에서 파일 경로 유추
        // URL: /uploads/yyyy/MM/dd/filename.jpg
        // File: {uploadBasePath}/yyyy/MM/dd/filename.jpg
        
        try {
            if (StringUtils.hasText(user.getProfileImageUrl())) {
                deleteFile(user.getProfileImageUrl());
            }
            if (StringUtils.hasText(user.getThumbnailUrl())) {
                deleteFile(user.getThumbnailUrl());
            }
        } catch (Exception e) {
            log.warn("기존 이미지 삭제 실패: {}", e.getMessage());
            // 삭제 실패가 전체 로직을 실패하게 하면 안됨 (로그만 남김)
        }
    }

    private void deleteFile(String fileUrl) {
        // "/uploads/" 제거 또는 경로 매핑에 따라 조정
        // 예: fileUrl이 "/uploads/..."로 시작한다고 가정
        // uploadBasePath가 "uploads"라면, fileUrl의 "/uploads/" 부분과 겹칠 수 있음.
        // 단순히 Path.of(uploadBasePath).getParent().resolve(fileUrl.substring(1)) 이런 식일 수도 있음.
        // 여기서는 uploadBasePath가 절대경로거나 실행위치 기준 상대경로라고 가정.
        
        // fileUrl: /uploads/2026/01/12/xxx.jpg
        // uploadBasePath: uploads
        
        String relativePath = fileUrl;
        if (fileUrl.startsWith("/uploads/")) {
            relativePath = fileUrl.substring("/uploads/".length());
        } else if (fileUrl.startsWith("uploads/")) {
             relativePath = fileUrl.substring("uploads/".length());
        }

        Path filePath = Paths.get(uploadBasePath, relativePath).toAbsolutePath();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("파일 삭제 실패: {}", filePath);
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "jpg"; // default
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}

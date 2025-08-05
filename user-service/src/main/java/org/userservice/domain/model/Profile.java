package org.userservice.domain.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

/**
 * 사용자 프로필 Value Object
 * 자기소개와 프로필 이미지 정보를 관리
 */
public record Profile(Optional<String> bio, Optional<URI> profileImageUrl) {
    
    private static final int MAX_BIO_LENGTH = 160;
    private static final String DEFAULT_PROFILE_IMAGE = "https://default-profile-image.com/default.png";
    
    public Profile {
        // bio 검증
        bio.ifPresent(bioText -> {
            if (bioText.trim().isEmpty()) {
                throw new IllegalArgumentException("자기소개는 빈 값일 수 없습니다");
            }
            
            if (bioText.length() > MAX_BIO_LENGTH) {
                throw new IllegalArgumentException("자기소개는 " + MAX_BIO_LENGTH + "자를 초과할 수 없습니다");
            }
            
            if (containsInappropriateContent(bioText)) {
                throw new IllegalArgumentException("부적절한 내용이 포함되어 있습니다");
            }
        });
        
        // profileImageUrl 검증
        profileImageUrl.ifPresent(this::validateImageUrl);
    }
    
    /**
     * 기본 프로필 생성 (빈 자기소개, 기본 이미지)
     */
    public static Profile createDefault() {
        try {
            return new Profile(
                Optional.empty(),
                Optional.of(new URI(DEFAULT_PROFILE_IMAGE))
            );
        } catch (URISyntaxException e) {
            throw new IllegalStateException("기본 프로필 이미지 URL이 유효하지 않습니다", e);
        }
    }
    
    /**
     * 자기소개만 있는 프로필 생성
     */
    public static Profile withBio(String bio) {
        return new Profile(
            Optional.ofNullable(bio).filter(b -> !b.trim().isEmpty()),
            Optional.empty()
        );
    }
    
    /**
     * 프로필 이미지만 있는 프로필 생성
     */
    public static Profile withImageUrl(String imageUrl) {
        try {
            return new Profile(
                Optional.empty(),
                Optional.of(new URI(imageUrl))
            );
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("유효하지 않은 이미지 URL입니다: " + imageUrl, e);
        }
    }
    
    /**
     * 완전한 프로필 생성
     */
    public static Profile of(String bio, String imageUrl) {
        try {
            return new Profile(
                Optional.ofNullable(bio).filter(b -> !b.trim().isEmpty()),
                Optional.ofNullable(imageUrl).map(url -> {
                    try {
                        return new URI(url);
                    } catch (URISyntaxException e) {
                        throw new IllegalArgumentException("유효하지 않은 이미지 URL입니다: " + url, e);
                    }
                })
            );
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
    
    /**
     * 자기소개 업데이트
     */
    public Profile updateBio(String newBio) {
        return new Profile(
            Optional.ofNullable(newBio).filter(b -> !b.trim().isEmpty()),
            this.profileImageUrl
        );
    }
    
    /**
     * 프로필 이미지 업데이트
     */
    public Profile updateProfileImage(String newImageUrl) {
        try {
            return new Profile(
                this.bio,
                Optional.ofNullable(newImageUrl).map(url -> {
                    try {
                        return new URI(url);
                    } catch (URISyntaxException e) {
                        throw new IllegalArgumentException("유효하지 않은 이미지 URL입니다: " + url, e);
                    }
                })
            );
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }
    
    /**
     * 자기소개 반환 (없을 경우 빈 문자열)
     */
    public String getBioOrEmpty() {
        return bio.orElse("");
    }
    
    /**
     * 프로필 이미지 URL 반환 (없을 경우 기본 이미지)
     */
    public String getProfileImageUrlOrDefault() {
        return profileImageUrl
            .map(URI::toString)
            .orElse(DEFAULT_PROFILE_IMAGE);
    }
    
    /**
     * 자기소개 글자 수 반환
     */
    public int getBioLength() {
        return bio.map(String::length).orElse(0);
    }
    
    /**
     * 남은 자기소개 글자 수
     */
    public int getRemainingBioLength() {
        return MAX_BIO_LENGTH - getBioLength();
    }
    
    /**
     * 자기소개가 있는지 확인
     */
    public boolean hasBio() {
        return bio.isPresent();
    }
    
    /**
     * 사용자 정의 프로필 이미지가 있는지 확인
     */
    public boolean hasCustomProfileImage() {
        return profileImageUrl
            .map(uri -> !uri.toString().equals(DEFAULT_PROFILE_IMAGE))
            .orElse(false);
    }
    
    /**
     * 프로필이 완성되었는지 확인 (자기소개와 프로필 이미지 모두 있음)
     */
    public boolean isComplete() {
        return hasBio() && hasCustomProfileImage();
    }
    
    /**
     * 이미지 URL 유효성 검증
     */
    private void validateImageUrl(URI imageUrl) {
        String scheme = imageUrl.getScheme();
        if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
            throw new IllegalArgumentException("프로필 이미지는 HTTP/HTTPS URL이어야 합니다");
        }
        
        String path = imageUrl.getPath();
        if (path != null && !isValidImageExtension(path)) {
            throw new IllegalArgumentException("지원하지 않는 이미지 형식입니다");
        }
    }
    
    /**
     * 지원되는 이미지 확장자 확인
     */
    private boolean isValidImageExtension(String path) {
        String lowerPath = path.toLowerCase();
        return lowerPath.endsWith(".jpg") || 
               lowerPath.endsWith(".jpeg") || 
               lowerPath.endsWith(".png") || 
               lowerPath.endsWith(".gif") ||
               lowerPath.endsWith(".webp");
    }
    
    /**
     * 부적절한 내용 포함 여부 확인 (간단한 예시)
     */
    private boolean containsInappropriateContent(String text) {
        // 실제로는 더 정교한 필터링 로직 또는 외부 서비스 연동
        String lowerText = text.toLowerCase();
        String[] inappropriateWords = {"spam", "광고", "도박"};
        
        for (String word : inappropriateWords) {
            if (lowerText.contains(word)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("Profile{bio='%s', profileImage='%s'}", 
            getBioOrEmpty(), 
            getProfileImageUrlOrDefault());
    }
    
    /**
     * 표시용 요약 정보
     */
    public ProfileSummary toSummary() {
        return new ProfileSummary(
            getBioOrEmpty(),
            getProfileImageUrlOrDefault(),
            getBioLength(),
            isComplete()
        );
    }
    
    /**
     * 프로필 요약 정보 레코드
     */
    public record ProfileSummary(
        String bio,
        String profileImageUrl,
        int bioLength,
        boolean isComplete
    ) {}
}
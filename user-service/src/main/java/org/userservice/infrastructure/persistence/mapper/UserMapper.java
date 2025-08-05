package org.userservice.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import org.userservice.domain.model.*;
import org.userservice.infrastructure.persistence.entity.UserJpaEntity;

import java.net.URI;
import java.util.Optional;

/**
 * 도메인 모델과 JPA 엔티티 간의 변환을 담당하는 매퍼
 */
@Component
public class UserMapper {

    /**
     * 도메인 모델을 JPA 엔티티로 변환
     */
    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
                .username(user.getUsername().value())
                .email(user.getEmail().value())
                .password(user.getPassword().encryptedValue())
                .profileBio(user.getProfile().getBioOrEmpty())
                .profileImageUrl(user.getProfile().getProfileImageUrlOrDefault())
                .followerCount(0)  // 기본값
                .followingCount(0) // 기본값
                .tweetCount(0)     // 기본값
                .emailVerified(false) // 기본값
                .active(true)      // 기본값
                .locked(false)     // 기본값
                .build();
    }

    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    public User toDomainModel(UserJpaEntity entity) {
        return new User(
                new UserId(entity.getId()),
                new Username(entity.getUsername()),
                new Email(entity.getEmail()),
                new Password(entity.getPassword()),
                createProfile(entity.getProfileBio(), entity.getProfileImageUrl()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isDeleted()
        );
    }

    /**
     * JPA 엔티티의 프로필 정보로부터 도메인 Profile 객체 생성
     */
    private Profile createProfile(String profileBio, String profileImageUrl) {
        Optional<String> bio = Optional.ofNullable(profileBio)
                .filter(b -> !b.trim().isEmpty());
        
        Optional<URI> imageUrl = Optional.ofNullable(profileImageUrl)
                .filter(url -> !url.trim().isEmpty())
                .map(url -> {
                    try {
                        return URI.create(url);
                    } catch (IllegalArgumentException e) {
                        // 잘못된 URL인 경우 기본값 사용
                        return null;
                    }
                })
                .filter(uri -> uri != null);
        
        return new Profile(bio, imageUrl);
    }
}

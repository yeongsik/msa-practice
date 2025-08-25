package org.userservice.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.userservice.domain.event.FollowEventDto;
import org.userservice.domain.event.FollowRemovedEvent;
import org.userservice.domain.event.FollowRemovedEventDto;
import org.userservice.domain.model.UserId;
import org.userservice.infrastructure.cache.UserStatsCacheRepository;

@Slf4j
@Component
public class UserStatsCacheUpdater {

    private final UserStatsCacheRepository userStatusCacheRepository;

    public UserStatsCacheUpdater(UserStatsCacheRepository userStatusCacheRepository) {
        this.userStatusCacheRepository = userStatusCacheRepository;
    }

    @KafkaListener(topics = "user-follow-events", groupId = "user-stats-cache-group")
    public void handleFollowEvent(FollowEventDto dto) {
        log.info("팔로우 이벤트 수신: {}", dto);

        UserId followerId = dto.getFollowerId();
        UserId followingId = dto.getFollowingId();

        userStatusCacheRepository.incrementFollowingCount(followerId.toString());
        userStatusCacheRepository.incrementFollowerCount(followingId.toString());
    }

    @KafkaListener(topics = "user-unfollow-events", groupId = "user-stats-cache-group")
    public void handleUnfollowEvent(FollowRemovedEventDto dto) {
        log.info("언팔로우 이벤트 수신: {}", dto);

        UserId followerId = dto.getFollowerId();
        UserId followingId = dto.getFollowingId();

        userStatusCacheRepository.decrementFollowingCount(followerId.toString());
        userStatusCacheRepository.decrementFollowerCount(followingId.toString());
    }
}

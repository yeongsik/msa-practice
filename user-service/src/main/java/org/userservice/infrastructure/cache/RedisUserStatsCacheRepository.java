package org.userservice.infrastructure.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.userservice.domain.model.UserId;

@Repository
public class RedisUserStatsCacheRepository implements UserStatsCacheRepository {

    private static final String FOLLOWING_KEY_PREFIX = "user:following:count:";
    private static final String FOLLOWER_KEY_PREFIX = "user:follower:count:";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUserStatsCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void incrementFollowingCount(String userId) {
        redisTemplate.opsForValue().increment(FOLLOWING_KEY_PREFIX + userId);
    }

    @Override
    public void incrementFollowerCount(String userId) {
        redisTemplate.opsForValue().increment(FOLLOWER_KEY_PREFIX + userId);

    }

    @Override
    public void decrementFollowingCount(String userId) {
        redisTemplate.opsForValue().decrement(FOLLOWING_KEY_PREFIX + userId);
    }

    @Override
    public void decrementFollowerCount(String userId) {
        redisTemplate.opsForValue().decrement(FOLLOWER_KEY_PREFIX + userId);
    }
}

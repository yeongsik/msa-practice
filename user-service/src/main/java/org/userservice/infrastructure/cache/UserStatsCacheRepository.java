package org.userservice.infrastructure.cache;

public interface UserStatsCacheRepository {

    void incrementFollowingCount(String userId);
    void incrementFollowerCount(String userId);
    void decrementFollowingCount(String userId);
    void decrementFollowerCount(String userId);
}

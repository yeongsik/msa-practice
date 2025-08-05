package org.userservice.domain.service;

import org.springframework.stereotype.Service;
import org.userservice.domain.model.Follow;
import org.userservice.domain.model.User;

@Service
public class UserFollowService {


    /**
     * 팔로우 관계 생성
     * @param follower 팔로우를 요청하는 사용자
     * @param following 팔로우 대상 사용자
     * @return 생성된 팔로우 관계
     */
    public Follow createFollow(User follower, User following) {
        // 도메인 규칙 검증
        validateFollowRules(follower, following);
    }

    /**
     * 팔로우 규칙 검증
     */
    private void validateFollowRules(User follower, User following) {
        if (!following.canBeFollowedBy(follower)) {
            throw new IllegalArgumentException("팔로우할 수 없는 사용자입니다.");
        }

        // 이미 팔로우 중인지 확인
        if (isAlreadyFollwing(follower.getUserId(), following.getUserId())) {
            throw new IllegalArgumentException("이미 팔로우 중인 사용자입니다.");
        }
    }

    public boolean isMutualFollow(UserId user1, UserId user2) {
        return isFollowing(user1, user2) && isFollowing(user2, user1);
    }

}

package org.userservice.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.userservice.domain.event.FollowCreatedEvent;
import org.userservice.domain.event.FollowRemovedEvent;
import org.userservice.domain.model.Follow;
import org.userservice.domain.model.User;
import org.userservice.domain.model.UserId;
import org.userservice.domain.repository.FollowRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFollowService {

    private final FollowRepository followRepository;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * 팔로우 관계 생성
     * @param follower 팔로우를 요청하는 사용자
     * @param following 팔로우 대상 사용자
     * @return 생성된 팔로우 관계
     */
    @Transactional
    public Follow createFollow(User follower, User following) {
        // 도메인 규칙 검증
        validateFollowRules(follower, following);
        
        // 팔로우 관계 생성 및 저장
        Follow follow = Follow.create(follower.getUserId(), following.getUserId());
        Follow save = followRepository.save(follow);

        // 팔로우 알림
        eventPublisher.publishEvent(new FollowCreatedEvent(this, follower.getUserId(), following.getUserId()));

        return save;
    }

    /**
     * 팔로우 규칙 검증
     */
    private void validateFollowRules(User follower, User following) {
        if (!following.canBeFollowedBy(follower)) {
            throw new IllegalArgumentException("팔로우할 수 없는 사용자입니다.");
        }

        // 이미 팔로우 중인지 확인
        if (isAlreadyFollowing(follower.getUserId(), following.getUserId())) {
            throw new IllegalArgumentException("이미 팔로우 중인 사용자입니다.");
        }
    }

    public boolean isMutualFollow(UserId user1, UserId user2) {
        return isFollowing(user1, user2) && isFollowing(user2, user1);
    }
    
    /**
     * 이미 팔로우 중인지 확인
     */
    private boolean isAlreadyFollowing(UserId follower, UserId following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    /**
     * 팔로우 여부 확인
     */
    private boolean isFollowing(UserId follower, UserId following) {
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
    
    /**
     * 팔로우 관계 해제
     */
    @Transactional
    public void unfollow(User follower, User following) {
        followRepository.findByFollowerAndFollowing(follower.getUserId(), following.getUserId())
                .ifPresent(follow -> {
                    follow.unfollow();
                    followRepository.save(follow);

                    // 언팔로우 이벤트 발행
                    eventPublisher.publishEvent(new FollowRemovedEvent(this, follower.getUserId(), following.getUserId()));
                });
    }
    
    /**
     * 사용자의 팔로잉 수 조회
     */
    public long getFollowingCount(UserId userId) {
        return followRepository.countByFollower(userId);
    }
    
    /**
     * 사용자의 팔로워 수 조회
     */
    public long getFollowerCount(UserId userId) {
        return followRepository.countByFollowing(userId);
    }

}

package org.userservice.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.userservice.domain.model.UserId;
import org.userservice.domain.service.UserFollowService;

@Getter
public class FollowRemovedEvent extends ApplicationEvent {

    private final UserId followerId;
    private final UserId followingId;

    public FollowRemovedEvent(Object source, UserId followerId, UserId followingId) {
        super(source);
        this.followerId = followerId;
        this.followingId = followingId;
    }
}

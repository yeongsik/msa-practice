package org.userservice.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.userservice.domain.model.UserId;

@Getter
public class FollowCreatedEvent extends ApplicationEvent {

    private final UserId followerId;
    private final UserId followingId;

    public FollowCreatedEvent(Object source, UserId followerId, UserId followingId) {
        super(source);
        this.followerId = followerId;
        this.followingId = followingId;
    }
}

package org.userservice.domain.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.userservice.domain.model.UserId;

@Getter
@RequiredArgsConstructor
public class FollowEventDto {
    private final UserId followerId;
    private final UserId followingId;
}

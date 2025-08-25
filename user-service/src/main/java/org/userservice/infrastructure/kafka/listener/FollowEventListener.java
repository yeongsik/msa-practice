package org.userservice.infrastructure.kafka.listener;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.userservice.domain.event.FollowCreatedEvent;
import org.userservice.domain.event.FollowEventDto;
import org.userservice.domain.event.FollowRemovedEvent;
import org.userservice.domain.event.FollowRemovedEventDto;

@Component
public class FollowEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public FollowEventListener(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    @EventListener
    public void handleFollowCreatedEvent(FollowCreatedEvent event) {
        // Kafka 메시지 객체 생성
        FollowEventDto dto = new FollowEventDto(event.getFollowerId(), event.getFollowingId());
        kafkaTemplate.send("user-follow-events", dto);
    }

    @Async
    @EventListener
    public void handleFollowRemovedEvent(FollowRemovedEvent event) {
        // Kafka 메시지 객체 생성
        FollowRemovedEventDto dto = new FollowRemovedEventDto(event.getFollowerId(), event.getFollowingId());
        kafkaTemplate.send("user-unfollow-events", dto);
    }
}

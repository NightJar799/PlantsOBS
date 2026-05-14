package org.auth.service;

import org.plantrmq.EventEnvelope;
import org.plantrmq.RoutingKeys;
import org.plantrmq.UserEvent;
import org.auth.model.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public EventPublisherService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreatedEvent(User user) {
        UserEvent.Created event = new UserEvent.Created(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhoneNumber());

        EventEnvelope<UserEvent.Created> envelope = EventEnvelope.wrap(event, "auth-service", "user.created");

        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.USER_CREATED, envelope);
    }

    public void publishUserLoginEvent(User user) {
        UserEvent.Updated event = new UserEvent.Updated(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhoneNumber());

        EventEnvelope<UserEvent.Updated> envelope = EventEnvelope.wrap(event, "auth-service", "user.updated");

        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.USER_UPDATED, envelope);
    }
}
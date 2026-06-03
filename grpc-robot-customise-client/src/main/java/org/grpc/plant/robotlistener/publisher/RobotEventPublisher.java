package org.grpc.plant.robotlistener.publisher;

import org.plantrmq.EventEnvelope;
import org.plantrmq.RobotEvent;
import org.plantrmq.RoutingKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RobotEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RobotEventPublisher.class);
    private static final String SOURCE = "grpc-robot-customise-client";

    private final RabbitTemplate rabbitTemplate;

    public RobotEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishRobotUpdated(RobotEvent.Updated updatedEvent) {
        try {
            EventEnvelope<RobotEvent> envelope = EventEnvelope.wrap(updatedEvent, SOURCE, RoutingKeys.ROBOT_UPDATED);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.ROBOT_UPDATED, envelope);
            log.info("Опубликовано robot.updated: robotId={}, usedMetrics={}",
                    updatedEvent.robotId(), updatedEvent.usedCharacteristics());
        } catch (Exception e) {
            log.error("Не удалось опубликовать robot.updated: {}", e.getMessage());
        }
    }
}
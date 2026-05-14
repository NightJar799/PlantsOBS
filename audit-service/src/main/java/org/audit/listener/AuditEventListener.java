package org.audit.listener;

import org.audit.model.AuditEntry;
import org.audit.storage.AuditStorage;
import org.plantrmq.EventMetadata;
import org.plantrmq.HomePlantEvent;
import org.plantrmq.RobotEvent;
import org.plantrmq.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;

@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditStorage auditStorage;
    private final JsonMapper jsonMapper;

    public AuditEventListener(AuditStorage auditStorage, JsonMapper jsonMapper) {
        this.auditStorage = auditStorage;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.audit.events", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            if (auditStorage.isDuplicate(metadata.eventId())) {
                log.warn("Дубликат события пропущен: eventId={}", metadata.eventId());
                return;
            }

            JsonNode payloadNode = root.get("payload");
            String description = buildDescription(metadata.eventType(), payloadNode);

            AuditEntry entry = auditStorage.save(new AuditEntry(
                    0,
                    metadata.eventId(),
                    metadata.eventType(),
                    metadata.source(),
                    metadata.timestamp(),
                    Instant.now(),
                    description
            ));

            log.info("[AUDIT #{}] {} | {}", entry.sequenceNumber(), metadata.eventType(), description);

        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    private String buildDescription(String eventType, JsonNode payloadNode) throws Exception {
        return switch (eventType) {
            case "homeplant.created" -> {
                HomePlantEvent.Created e = jsonMapper.treeToValue(payloadNode, HomePlantEvent.Created.class);
                yield String.format("Создано растение «%s» (note: %s), владелец: %s",
                        e.name(), e.note(), e.userId());
            }
            case "homeplant.updated" -> {
               HomePlantEvent.Updated e = jsonMapper.treeToValue(payloadNode, HomePlantEvent.Updated.class);
                yield String.format("Обновлено растение «%s» (note: %s)",
                        e.name(), e.note());
            }
            case "homeplant.deleted" -> {
                HomePlantEvent.Deleted e = jsonMapper.treeToValue(payloadNode, HomePlantEvent.Deleted.class);
                yield String.format("Удаления растения id=%d «%s»", e.plantId(), e.name());
            }
            case "user.created" -> {
                UserEvent.Created e = jsonMapper.treeToValue(payloadNode, UserEvent.Created.class);
                yield String.format("Создан пользователь «%s» (телефон: %s)",
                        e.name(), e.phone());
            }
            case "user.updated" -> {
               UserEvent.Updated e = jsonMapper.treeToValue(payloadNode, UserEvent.Updated.class);
                yield String.format("Создан пользователь «%s» (телефон: %s)",
                        e.name(), e.phone());
            }
            case "user.deleted" -> {
                UserEvent.Deleted e = jsonMapper.treeToValue(payloadNode, UserEvent.Deleted.class);
                yield String.format("Удаления пользователя «%s» (почта: %s)",
                        e.name(), e.mail());
            }
            case "robot.created" -> {
                RobotEvent.Created e = jsonMapper.treeToValue(payloadNode, RobotEvent.Created.class);
                yield String.format("Создан датчик «%s» название датчика %s",
                        e.robotId(), e.name());
            }
            case "robot.updated" -> {
               RobotEvent.Updated e = jsonMapper.treeToValue(payloadNode, RobotEvent.Updated.class);
                yield String.format("Создан датчик «%s» название датчика %s",
                        e.robotId(), e.name());
            }
            case "robot.deleted" -> {
                RobotEvent.Deleted e = jsonMapper.treeToValue(payloadNode, RobotEvent.Deleted.class);
                yield String.format("Удаление датчик «%s» название датчика %s",
                        e.robotId(), e.name());
            }
            default -> "Неизвестное событие: " + eventType;
        };
    }
}

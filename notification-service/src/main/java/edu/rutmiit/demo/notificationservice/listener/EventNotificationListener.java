package edu.rutmiit.demo.notificationservice.listener;

import edu.rutmiit.demo.notificationservice.websocket.NotificationWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import org.plantrmq.*;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Слушатель всех доменных событий из RabbitMQ.
 *
 * Получает события из очереди q.notifications.all (binding "#"),
 * формирует человекочитаемое JSON-уведомление и рассылает
 * всем подключённым WebSocket-клиентам через NotificationWebSocketHandler.
 *
 * Дедупликация — по eventId (на случай повторной доставки RabbitMQ).
 */
@Component
public class EventNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationListener.class);

    private final NotificationWebSocketHandler webSocketHandler;
    private final JsonMapper jsonMapper;

    /** Набор обработанных eventId для дедупликации. */
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public EventNotificationListener(NotificationWebSocketHandler webSocketHandler,
                                     JsonMapper jsonMapper) {
        this.webSocketHandler = webSocketHandler;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.notifications.all", messageConverter = "")
    public void handleEvent(Message message) {
        try {
            byte[] body = message.getBody();
            JsonNode root = jsonMapper.readTree(body);

            // Парсим метаданные
            JsonNode metaNode = root.get("metadata");
            EventMetadata metadata = jsonMapper.treeToValue(metaNode, EventMetadata.class);

            // Дедупликация по eventId 
            if (!processedEventIds.add(metadata.eventId())) {
                log.warn("Дубликат уведомления пропущен: eventId={}", metadata.eventId());
                return;
            }

            // Формируем уведомление
            JsonNode payloadNode = root.get("payload");
            String title = buildTitle(metadata.eventType());
            String description = buildDescription(metadata.eventType(), payloadNode);
            String icon = resolveIcon(metadata.eventType());
            String level = resolveLevel(metadata.eventType());

            // JSON для WebSocket-клиента 
            String notificationJson = jsonMapper.writeValueAsString(
                    new NotificationPayload(
                            "NOTIFICATION",
                            metadata.eventId(),
                            metadata.eventType(),
                            title,
                            description,
                            icon,
                            level,
                            metadata.source(),
                            metadata.timestamp().toString(),
                            Instant.now().toString()
                    )
            );

            // Broadcast в WebSocket
            webSocketHandler.broadcast(notificationJson);

            log.info("[NOTIFY] {} | {} (клиентов: {})",
                    metadata.eventType(), description, webSocketHandler.getActiveConnectionCount());

        } catch (Exception e) {
            log.error("Ошибка обработки события для уведомлений: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось обработать событие", e);
        }
    }

    // Формирование заголовка уведомления

    private String buildTitle(String eventType) {
        return switch (eventType) {
            case "homeplant.created" -> "Новое растение";
            case "robot.created"     -> "Новый датчик";
            case "homeplant.updated" -> "Обновление растения";
            case "robot.updated"     -> "Обновление датчика";
            case "homeplant.deleted" -> "Удаление растения";
            case "robot.deleted"     -> "Удаление датчика";
            default -> "Событие: " + eventType;
        };
    }

    // Формирование описания

    private String buildDescription(String eventType, JsonNode payloadNode) {
        try {
            return switch (eventType) {
            case "homeplant.created" -> {
                HomePlantEvent.Created e = jsonMapper.treeToValue(payloadNode, HomePlantEvent.Created.class);
                yield String.format("Создано растение «%s» (note: %s)",
                        e.name(), e.note());
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
            case "robot.created" -> {
                RobotEvent.Created e = jsonMapper.treeToValue(payloadNode, RobotEvent.Created.class);
                yield String.format("Создан датчик «%s» название датчика %s",
                        e.robotId(), e.name());
            }
            case "robot.updated" -> {
               RobotEvent.Updated e = jsonMapper.treeToValue(payloadNode, RobotEvent.Updated.class);
                yield String.format("Обнавлён датчик «%s» название датчика %s",
                        e.robotId(), e.name());
            }
            case "robot.deleted" -> {
                RobotEvent.Deleted e = jsonMapper.treeToValue(payloadNode, RobotEvent.Deleted.class);
                yield String.format("Удаление датчик «%s» название датчика %s",
                        e.robotId(), e.name());
            }
            default -> "Неизвестное событие: " + eventType;
        };
        } catch (Exception e) {
            return "Событие " + eventType + " (ошибка парсинга)";
        }
    }

    // Иконка по типу события

    private String resolveIcon(String eventType) {
        return switch (eventType) {
            case "homeplant.created" -> "homeplant-plus";
            case "robot.created"     -> "robot-plus";
            case "homeplant.updated" -> "homeplant-edit";
            case "robot.updated"     -> "robot-edit";
            case "homeplant.deleted" -> "homeplant-remove";
            case "robot.deleted"     -> "robot-remove";
            default                  -> "bell";
        };
    }

    // Уровень уведомления

    private String resolveLevel(String eventType) {
        return switch (eventType) {
            case "homeplant.deleted", "homeplant.update"           -> "warning";
            case "robot.created", "robot.updated", "robot.deleted" -> "info";
            default                                                -> "success";
        };
    }

    /**
     * Payload уведомления для WebSocket.
     */
    record NotificationPayload(
            String type,
            String eventId,
            String eventType,
            String title,
            String description,
            String icon,
            String level,
            String source,
            String eventTimestamp,
            String receivedAt
    ) {}
}

package org.grpc.plant.robotlistener.listener;

import org.plantrmq.EventMetadata;
import org.plantrmq.RobotEvent;
import org.robotContract.grpc.RobotCustomisationRequest;
import org.robotContract.grpc.RobotCustomisationResponse;
import org.robotContract.grpc.RobotCustomiseGrpc;
import org.grpc.plant.robotlistener.publisher.RobotEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RobotEventListener {

    private static final Logger log = LoggerFactory.getLogger(RobotEventListener.class);

    private final RobotCustomiseGrpc.RobotCustomiseBlockingStub robotStub;
    private final RobotEventPublisher eventPublisher;
    private final JsonMapper jsonMapper;

    private final ConcurrentHashMap<Long, Long> robotToPlant = new ConcurrentHashMap<>();

    public RobotEventListener(RobotCustomiseGrpc.RobotCustomiseBlockingStub robotStub,
                              RobotEventPublisher eventPublisher,
                              JsonMapper jsonMapper) {
        this.robotStub = robotStub;
        this.eventPublisher = eventPublisher;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = "q.robot-customise.created", messageConverter = "")
    public void handleRobotCreated(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.getBody());
            EventMetadata meta = jsonMapper.treeToValue(root.get("metadata"), EventMetadata.class);
            RobotEvent.Created created = jsonMapper.treeToValue(root.get("payload"), RobotEvent.Created.class);

            log.info("Получено robot.created: robotId={}, plantId={}", created.robotId(), created.plantId());

            robotToPlant.put(created.robotId(), created.plantId());

            RobotCustomisationRequest request = RobotCustomisationRequest.newBuilder()
                    .setRobotId(created.robotId())
                    .setName(created.name())
                    .setMeasurment(created.measuredCharacteristic())
                    .setPlantId(created.plantId())
                    .setChosenMeasurment("")
                    .build();

            RobotCustomisationResponse response = robotStub.changeRobotMeasurment(request);
            if (!response.getChosenMeasurment().isBlank()) {
                publishUpdated(created.robotId(), created.name(),
                        created.sensorType(), created.measuredCharacteristic(),
                        response.getChosenMeasurment());
            } else {
                log.warn("Не удалось назначить метрику для robotId={}", created.robotId());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки robot.created: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = "q.robot-customise.updated", messageConverter = "")
    public void handleRobotUpdated(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.getBody());
            EventMetadata meta = jsonMapper.treeToValue(root.get("metadata"), EventMetadata.class);
            if ("grpc-robot-customise-client".equals(meta.source())) {
                log.debug("Пропускаем своё же событие robot.updated");
                return;
            }
            RobotEvent.Updated updated = jsonMapper.treeToValue(root.get("payload"), RobotEvent.Updated.class);

            Long plantId = robotToPlant.get(updated.robotId());
            if (plantId == null) {
                log.error("Неизвестный robotId={}, пропускаем", updated.robotId());
                return;
            }

            log.info("Получено robot.updated: robotId={}, usedMetrics={}", updated.robotId(), updated.usedCharacteristics());

            RobotCustomisationRequest request = RobotCustomisationRequest.newBuilder()
                    .setRobotId(updated.robotId())
                    .setName(updated.name())
                    .setMeasurment(updated.measuredCharacteristic())
                    .setPlantId(plantId)
                    .setChosenMeasurment(updated.usedCharacteristics())
                    .build();

            RobotCustomisationResponse response = robotStub.changeRobotMeasurment(request);
            if (!response.getChosenMeasurment().isBlank() &&
                    !response.getChosenMeasurment().equals(updated.usedCharacteristics())) {
                publishUpdated(updated.robotId(), updated.name(),
                        updated.sensorType(), updated.measuredCharacteristic(),
                        response.getChosenMeasurment());
            }
        } catch (Exception e) {
            log.error("Ошибка обработки robot.updated: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private void publishUpdated(long robotId, String name, String sensorType,
                                String measuredCharacteristic, String usedMetrics) {
        RobotEvent.Updated event = new RobotEvent.Updated(
                robotId, name, sensorType, measuredCharacteristic, usedMetrics
        );
        eventPublisher.publishRobotUpdated(event);
    }
}
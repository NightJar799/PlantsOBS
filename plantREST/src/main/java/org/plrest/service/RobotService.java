package org.plrest.service;

import org.obs.dto.GrowthCharResponse;
import org.obs.dto.RobotRequest;
import org.obs.dto.RobotResponse;
import org.obs.exceptions.ResourceNotFoundException;
import org.plantrmq.EventEnvelope;
import org.plantrmq.RobotEvent;
import org.plantrmq.RoutingKeys;
import org.plrest.storage.InMemoryStorage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RobotService {

    private final InMemoryStorage storage;
    private final PlantService plantService;
    private final RabbitTemplate rabbitTemplate;

    public RobotService(InMemoryStorage storage, PlantService plantService, RabbitTemplate rabbitTemplate) {
        this.storage = storage;
        this.plantService = plantService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public RobotResponse findById(Long id) {
        RobotResponse robot = storage.robots.get(id);
        if (robot == null) {
            throw new ResourceNotFoundException("Robot", id);
        }
        return robot;
    }

    public List<RobotResponse> findByPlantId(Long plantId) {
        plantService.findById(plantId);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(plantId);
        List<RobotResponse> robots = plantRobots != null
                ? new ArrayList<>(plantRobots.values())
                : List.of();

        robots.sort(Comparator.comparingLong(RobotResponse::getId));

        return robots;
    }

        public RobotResponse createAndBind(Long plantId, RobotRequest request) {
        plantService.findById(plantId);
        long id = storage.robotSequence.incrementAndGet();
        RobotResponse robot = RobotResponse.builder()
                .id(id)
                .sensorType(request.sensorType())
                .usedCharacteristic(request.usedCharacteristic())
                .measuredCharacteristic(request.measuredCharacteristic())
                .name(request.name())
                .plantId(plantId)
                .build();
        storage.robots.put(id, robot);
        storage.plantRobots.computeIfAbsent(plantId, k -> new ConcurrentHashMap<>()).put(id, robot);

        RobotEvent.Created event = new RobotEvent.Created(
                id,
                robot.getName(),
                String.valueOf(robot.getSensorType()),
                robot.getMeasuredCharacteristic(),
                robot.getUsedCharacteristic(),
                plantId
        );
        EventEnvelope<RobotEvent> envelope = EventEnvelope.wrap(
                event,
                "plantOBS",
                RoutingKeys.ROBOT_CREATED
        );
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.ROBOT_CREATED, envelope);

        return robot;
    }

    public GrowthCharResponse addRobotToPlant(Long plantId, Long robotId) {
        plantService.findById(plantId);
        RobotResponse robot = findById(robotId);

        if (!storage.plantRobots.computeIfAbsent(plantId, k -> new ConcurrentHashMap<>())
                .containsKey(robotId)) {
            storage.plantRobots.get(plantId).put(robotId, robot);
        }

        Long latestGrowthCharId = storage.robotLatestGrowthChar.get(robotId);
        if (latestGrowthCharId != null) {
            GrowthCharResponse latest = storage.growthChars.get(latestGrowthCharId);
            if (latest != null) {
                return latest;
            }
        }

        RobotEvent.Updated event = new RobotEvent.Updated(
                robotId,
                robot.getName(),
                String.valueOf(robot.getSensorType()),
                robot.getMeasuredCharacteristic(),
                robot.getUsedCharacteristic()
        );
        EventEnvelope<RobotEvent> envelope = EventEnvelope.wrap(
                event,
                "plantOBS",
                RoutingKeys.ROBOT_UPDATED
        );
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.ROBOT_UPDATED, envelope);

        throw new ResourceNotFoundException("No growth data for robot", robotId);
    }

    public RobotResponse unbind(Long robotId, Long plantId) {
        plantService.findById(plantId);
        RobotResponse robot = findById(robotId);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(plantId);
        if (plantRobots != null && plantRobots.remove(robotId) != null) {
        }

        RobotEvent.Deleted event = new RobotEvent.Deleted(
                robotId,
                robot.getName());
        
        EventEnvelope<RobotEvent> envelope = EventEnvelope.wrap(
                event,
                "plantOBS",
                RoutingKeys.ROBOT_DELETED
        );
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.ROBOT_DELETED, envelope);
        return robot;
    }

    public RobotResponse replace(Long plantId, Long robotId, RobotRequest request) {
        plantService.findById(plantId);
        findById(robotId);

        RobotResponse updated = RobotResponse.builder()
                .id(robotId)
                .sensorType(request.sensorType())
                .usedCharacteristic(request.usedCharacteristic())
                .measuredCharacteristic(request.measuredCharacteristic())
                .name(request.name())
                .plantId(plantId)
                .build();
        storage.robots.put(robotId, updated);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(plantId);
        if (plantRobots != null && plantRobots.containsKey(robotId)) {
            plantRobots.put(robotId, updated);
        }

        RobotEvent.Updated event = new RobotEvent.Updated(
                robotId,
                updated.getName(),
                String.valueOf(updated.getSensorType()),
                updated.getMeasuredCharacteristic(),
                updated.getUsedCharacteristic()
        );
        EventEnvelope<RobotEvent> envelope = EventEnvelope.wrap(
                event,
                "plantOBS",
                RoutingKeys.ROBOT_UPDATED
        );
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.ROBOT_UPDATED, envelope);

        return updated;
    }

    public RobotResponse patch(Long plantId, Long robotId, RobotRequest request) {
        plantService.findById(plantId);
        RobotResponse existing = findById(robotId);

        RobotResponse patched = RobotResponse.builder()
                .id(robotId)
                .sensorType(request.sensorType())
                .usedCharacteristic(request.usedCharacteristic())
                .measuredCharacteristic(request.measuredCharacteristic())
                .name(request.name())
                .plantId(plantId)
                .build();
        storage.robots.put(robotId, patched);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(plantId);
        if (plantRobots != null && plantRobots.containsKey(robotId)) {
            plantRobots.put(robotId, patched);
        }

        RobotEvent.Updated event = new RobotEvent.Updated(
                robotId,
                patched.getName(),
                String.valueOf(patched.getSensorType()),
                patched.getMeasuredCharacteristic(),
                patched.getUsedCharacteristic()
        );
        EventEnvelope<RobotEvent> envelope = EventEnvelope.wrap(
                event,
                "plantOBS",
                RoutingKeys.ROBOT_UPDATED
        );
        rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, RoutingKeys.ROBOT_UPDATED, envelope);

        return patched;
    }
}
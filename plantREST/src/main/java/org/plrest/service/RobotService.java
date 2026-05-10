package org.plrest.service;

import org.obs.dto.GrowthCharResponse;
import org.obs.dto.RobotRequest;
import org.obs.dto.RobotResponse;
import org.obs.exceptions.ResourceNotFoundException;
import org.plrest.storage.InMemoryStorage;
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

    public RobotService(InMemoryStorage storage, PlantService plantService) {
        this.storage = storage;
        this.plantService = plantService;
    }

    public RobotResponse findById(Long id) {
        RobotResponse robot = storage.robots.get(id);
        if (robot == null) {
            throw new ResourceNotFoundException("Robot", id);
        }
        return robot;
    }

    public Page<RobotResponse> findByPlantId(Long plantId, int page, int size) {
        plantService.findById(plantId);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(plantId);
        List<RobotResponse> robots = plantRobots != null
                ? new ArrayList<>(plantRobots.values())
                : List.of();

        robots.sort(Comparator.comparingLong(RobotResponse::getId));

        int start = page * size;
        int end = Math.min(start + size, robots.size());
        List<RobotResponse> content = start >= robots.size() ? List.of() : robots.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(page, size), robots.size());
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
        storage.plantRobots.computeIfAbsent(plantId, k -> new ConcurrentHashMap<>())
                .put(id, robot);

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

        throw new ResourceNotFoundException("No growth data for robot", robotId);
    }

    public void unbind(Long robotId, Long plantId) {
        plantService.findById(plantId);
        findById(robotId);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(plantId);
        if (plantRobots != null && plantRobots.remove(robotId) != null) {
        }
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

        return patched;
    }
}
package org.plrest.service;

import org.obs.dto.GrowthCharRequest;
import org.obs.dto.GrowthCharResponse;
import org.obs.exceptions.ResourceNotFoundException;
import org.plrest.storage.InMemoryStorage;
import org.springframework.stereotype.Service;

@Service
public class GrowthCharService {

    private final InMemoryStorage storage;
    private final PlantService plantService;

    public GrowthCharService(InMemoryStorage storage, PlantService plantService) {
        this.storage = storage;
        this.plantService = plantService;
    }

    public GrowthCharResponse findByPlantId(Long plantId) {
        plantService.findById(plantId);

        return storage.growthChars.values().stream()
                .filter(gc -> gc.getId().equals(plantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Growth characteristics for plant", plantId));
    }

    public GrowthCharResponse submitReport(Long plantId, GrowthCharRequest request) {
        plantService.findById(plantId);

        long id = storage.growthCharSequence.incrementAndGet();
        GrowthCharResponse response = GrowthCharResponse.builder()
                .id(plantId)
                .lx(request.lx())
                .soilPh(request.soilPh())
                .air(request.air())
                .water(request.water())
                .heat(request.heat())
                .nitrogen(request.nitrogen())
                .humidity(request.humidity())
                .build();
        storage.growthChars.put(id, response);
        return response;
    }

    public void sendRecommendations(Long plantId, GrowthCharRequest request) {
        plantService.findById(plantId);
    }

    public GrowthCharResponse createFromRobot(Long plantId, Long robotId, GrowthCharRequest request) {
        plantService.findById(plantId);

        long id = storage.growthCharSequence.incrementAndGet();
        GrowthCharResponse response = GrowthCharResponse.builder()
                .id(plantId)
                .lx(request.lx())
                .soilPh(request.soilPh())
                .air(request.air())
                .water(request.water())
                .heat(request.heat())
                .nitrogen(request.nitrogen())
                .humidity(request.humidity())
                .build();
        storage.growthChars.put(id, response);
        storage.robotLatestGrowthChar.put(robotId, id);
        return response;
    }
}
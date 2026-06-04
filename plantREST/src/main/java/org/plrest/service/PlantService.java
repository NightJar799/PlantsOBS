package org.plrest.service;

import org.obs.dto.HomePlantRequest;
import org.obs.dto.HomePlantResponse;
import org.obs.dto.RobotResponse;
import org.obs.exceptions.ResourceNotFoundException;
import org.plrest.storage.InMemoryStorage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlantService {

    private final InMemoryStorage storage;
    private final PlantSampleService plantSampleService;

    public PlantService(InMemoryStorage storage, PlantSampleService plantSampleService) {
        this.storage = storage;
        this.plantSampleService = plantSampleService;
    }

    public Page<HomePlantResponse> findAllByUser(int page, int size) {
        List<HomePlantResponse> all = storage.homePlants.values().stream()
                .sorted(Comparator.comparingLong(HomePlantResponse::getId))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<HomePlantResponse> content = start >= all.size() ? List.of() : all.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    public HomePlantResponse findById(Long id) {
        HomePlantResponse plant = storage.homePlants.get(id);
        if (plant == null) {
            throw new ResourceNotFoundException("Home plant", id);
        }
        return plant;
    }

    public HomePlantResponse create(HomePlantRequest request) {
        long id = storage.homePlantSequence.incrementAndGet();
        HomePlantResponse plant = HomePlantResponse.builder()
                .id(request.id())
                .age(request.age())
                .species(request.species())
                .note(request.note())
                .name(request.name())
                .build();
        storage.homePlants.put(id, plant);
        return plant;
    }

    public HomePlantResponse update(Long id, HomePlantRequest request) {
        findById(id);

        HomePlantResponse updated = HomePlantResponse.builder()
                .id(request.id())
                .age(request.age())
                .species(request.species())
                .note(request.note())
                .name(request.name())
                .build();
        storage.homePlants.put(id, updated);
        return updated;
    }

    public HomePlantResponse patch(Long id, HomePlantRequest request) {
        HomePlantResponse existing = findById(id);

        HomePlantResponse patched = HomePlantResponse.builder()
                .id(request.id())
                .age(request.age())
                .species(request.species())
                .note(request.note())
                .name(request.name())
                .build();
        storage.homePlants.put(id, patched);
        return patched;
    }

    public HomePlantResponse delete(Long id) {
        HomePlantResponse homePlantResponse =  findById(id);

        ConcurrentHashMap<Long, RobotResponse> plantRobots = storage.plantRobots.get(id);
        if (plantRobots != null) {
            plantRobots.keySet().forEach(storage.robots::remove);
            storage.plantRobots.remove(id);
        }

        storage.growthChars.values().removeIf(gc -> gc.getId().equals(id));

        storage.homePlants.remove(id);

        return homePlantResponse;
    }

    public void linkToSample(Long plantId, Long sampleId) {
        plantSampleService.findById(sampleId);
        HomePlantResponse plant = findById(plantId);

        HomePlantResponse updated = HomePlantResponse.builder()
                .id(plant.getId())
                .age(plant.getAge())
                .species(plant.getSpecies())
                .note(plant.getNote())
                .name(plant.getName())
                .sampleId(sampleId)
                .build();
        storage.homePlants.put(plantId, updated);
    }
}
package org.plrest.service;

import org.obs.dto.PlantSampleRequest;
import org.obs.dto.PlantsSampleResponse;
import org.obs.exceptions.ResourceNotFoundException;
import org.plrest.storage.InMemoryStorage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class PlantSampleService {

    private final InMemoryStorage storage;

    public PlantSampleService(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Page<PlantsSampleResponse> findAll(int page, int size) {
        List<PlantsSampleResponse> all = storage.plantSamples.values().stream()
                .sorted(Comparator.comparingLong(PlantsSampleResponse::getId))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<PlantsSampleResponse> content = start >= all.size() ? List.of() : all.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(page, size), all.size());
    }

    public PlantsSampleResponse findById(Long id) {
        PlantsSampleResponse sample = storage.plantSamples.get(id);
        if (sample == null) {
            throw new ResourceNotFoundException("Plant sample", id);
        }
        return sample;
    }

    public PlantsSampleResponse create(PlantSampleRequest request) {
        long id = storage.plantSampleSequence.incrementAndGet();
        PlantsSampleResponse sample = PlantsSampleResponse.builder()
                .id(id)
                .type(request.type())
                .wikiUrl(request.wikiUrl())
                .fruiting(request.fruiting())
                .difficulty(request.difficulty())
                .flower(request.flower())
                .build();
        storage.plantSamples.put(id, sample);
        return sample;
    }

    public PlantsSampleResponse update(Long id, PlantSampleRequest request) {
        findById(id);

        PlantsSampleResponse updated = PlantsSampleResponse.builder()
                .id(id)
                .type(request.type())
                .wikiUrl(request.wikiUrl())
                .fruiting(request.fruiting())
                .difficulty(request.difficulty())
                .flower(request.flower())
                .build();
        storage.plantSamples.put(id, updated);
        return updated;
    }

    public PlantsSampleResponse patch(Long id, PlantSampleRequest request) {
        PlantsSampleResponse existing = findById(id);

        PlantsSampleResponse patched = PlantsSampleResponse.builder()
                .id(id)
                .type(request.type())
                .wikiUrl(request.wikiUrl())
                .fruiting(request.fruiting())
                .difficulty(request.difficulty())
                .flower(request.flower())
                .build();
        storage.plantSamples.put(id, patched);
        return patched;
    }

    public PlantsSampleResponse delete(Long id) {
        PlantsSampleResponse deleted = findById(id);
        storage.plantSamples.remove(id);
        return deleted;
    }

    public Page<PlantsSampleResponse> findByType(String type, int page, int size) {
        List<PlantsSampleResponse> filtered = storage.plantSamples.values().stream()
                .filter(sample -> sample.getType() != null && sample.getType().toLowerCase().contains(type.toLowerCase()))
                .sorted(Comparator.comparingLong(PlantsSampleResponse::getId))
                .toList();

        int start = page * size;
        int end = Math.min(start + size, filtered.size());
        List<PlantsSampleResponse> content = start >= filtered.size() ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(page, size), filtered.size());
    }
}
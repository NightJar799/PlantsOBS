package org.plrest.controllers;

import org.obs.dto.PlantSampleRequest;
import org.obs.dto.PlantsSampleResponse;
import org.obs.endpoints.AdminApi;
import org.plrest.service.PlantSampleService;
import org.plrest.assemblers.PlantSampleModelAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminApiController implements AdminApi {

    private final PlantSampleService plantSampleService;
    private final PlantSampleModelAssembler plantSampleModelAssembler;
    private final PagedResourcesAssembler<PlantsSampleResponse> pagedResourcesAssembler;

    public AdminApiController(PlantSampleService plantSampleService,
                              PlantSampleModelAssembler plantSampleModelAssembler,
                              PagedResourcesAssembler<PlantsSampleResponse> pagedResourcesAssembler) {
        this.plantSampleService = plantSampleService;
        this.plantSampleModelAssembler = plantSampleModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public PagedModel<EntityModel<PlantsSampleResponse>> getAllPlantSamples(int page, int size) {
        Page<PlantsSampleResponse> paged = plantSampleService.findAll(page, size);
        return pagedResourcesAssembler.toModel(paged, plantSampleModelAssembler);
    }

    @Override
    public EntityModel<PlantsSampleResponse> getPlantSampleById(Long id) {
        return plantSampleModelAssembler.toModel(plantSampleService.findById(id));
    }

    @Override
    public ResponseEntity<EntityModel<PlantsSampleResponse>> createSample(PlantSampleRequest request) {
        PlantsSampleResponse created = plantSampleService.create(request);
        EntityModel<PlantsSampleResponse> model = plantSampleModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .build();
    }

    @Override
    public ResponseEntity<EntityModel<PlantsSampleResponse>> deleteSample(Long plantId) {
        plantSampleService.delete(plantId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EntityModel<PlantsSampleResponse>> patchSample(Long plantId, PlantSampleRequest request) {
        PlantsSampleResponse updated = plantSampleService.patch(plantId, request);
        EntityModel<PlantsSampleResponse> model = plantSampleModelAssembler.toModel(updated);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<EntityModel<PlantsSampleResponse>> putSample(Long plantId, PlantSampleRequest request) {
        PlantsSampleResponse updated = plantSampleService.update(plantId, request);
        EntityModel<PlantsSampleResponse> model = plantSampleModelAssembler.toModel(updated);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
public ResponseEntity<String> test() {
    return ResponseEntity.ok("OK");
}
}
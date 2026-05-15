package org.plrest.controllers;

import org.obs.dto.*;
import org.obs.endpoints.PlantManagerApi;
import org.plrest.service.GrowthCharService;
import org.plrest.service.PlantService;
import org.plrest.service.RobotService;
import org.plrest.assemblers.GrowthCharModelAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlantManagerApiController implements PlantManagerApi {

    private final PlantService plantService;
    private final GrowthCharService growthCharService;
    private final RobotService robotService;
    private final GrowthCharModelAssembler growthCharModelAssembler;
    private final PagedResourcesAssembler<List<RobotResponse>> pagedResourcesAssembler;

    public PlantManagerApiController(PlantService plantService,
                                     GrowthCharService growthCharService,
                                     RobotService robotService,
                                     GrowthCharModelAssembler growthCharModelAssembler,
                                     PagedResourcesAssembler<List<RobotResponse>> pagedResourcesAssembler) {
        this.plantService = plantService;
        this.growthCharService = growthCharService;
        this.robotService = robotService;
        this.growthCharModelAssembler = growthCharModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public PagedModel<EntityModel<List<RobotResponse>>> getPlantEnvironmentData(Long plantId, int page) {
        plantService.findById(plantId);
        Page<RobotResponse> paged = robotService.findByPlantId(plantId, page, 20);
        Page<List<RobotResponse>> wrappedPage = paged.map(List::of);
        return pagedResourcesAssembler.toModel(wrappedPage);
    }

    @Override
    public EntityModel<GrowthCharResponse> getPlantCharacteristics(Long plantId) {
        return growthCharModelAssembler.toModel(growthCharService.findByPlantId(plantId));
    }

    @Override
    public ResponseEntity<EntityModel<GrowthCharResponse>> submitPlantReport(Long plantId, GrowthCharRequest request) {
        GrowthCharResponse report = growthCharService.submitReport(plantId, request);
        EntityModel<GrowthCharResponse> model = growthCharModelAssembler.toModel(report);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public ResponseEntity<Void> sendRecommendations(Long plantId, GrowthCharRequest request) {
        growthCharService.sendRecommendations(plantId, request);
        return ResponseEntity.accepted().build();
    }
}
package org.plrest.controllers;

import org.obs.dto.*;
import org.obs.endpoints.PlantManagerApi;
import org.plrest.service.GrowthCharService;
import org.plrest.service.PlantService;
import org.plrest.service.RobotService;
import org.plrest.assemblers.GrowthCharModelAssembler;
import org.plrest.assemblers.RobotModelAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
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
    private final RobotModelAssembler robotModelAssembler;

    public PlantManagerApiController(PlantService plantService,
                                     GrowthCharService growthCharService,
                                     RobotService robotService,
                                     GrowthCharModelAssembler growthCharModelAssembler,
                                     PagedResourcesAssembler<List<RobotResponse>> pagedResourcesAssembler,
                                     RobotModelAssembler robotModelAssembler) {
        this.plantService = plantService;
        this.growthCharService = growthCharService;
        this.robotService = robotService;
        this.growthCharModelAssembler = growthCharModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.robotModelAssembler = robotModelAssembler;
    }

    @Override
    public CollectionModel<RobotResponse> getPlantEnvironmentData(Long plantId) {
        List<RobotResponse> robots = robotService.findByPlantId(plantId);
        return CollectionModel.of(robots);
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
    public ResponseEntity<Void> getRecommendations(Long plantId) {
        growthCharService.sendRecommendations(plantId);
        return ResponseEntity.accepted().build();
    }
}
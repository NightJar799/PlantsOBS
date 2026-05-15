package org.plrest.controllers;

import org.obs.dto.GrowthCharResponse;
import org.obs.dto.RobotResponse;
import org.obs.endpoints.RobotApi;
import org.plrest.service.RobotService;
import org.plrest.service.PlantService;
import org.plrest.assemblers.RobotModelAssembler;
import org.plrest.assemblers.GrowthCharModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RobotApiController implements RobotApi {

    private final RobotService robotService;
    private final PlantService plantService;
    private final RobotModelAssembler robotModelAssembler;
    private final GrowthCharModelAssembler growthCharModelAssembler;

    public RobotApiController(RobotService robotService,
                              PlantService plantService,
                              RobotModelAssembler robotModelAssembler,
                              GrowthCharModelAssembler growthCharModelAssembler) {
        this.robotService = robotService;
        this.plantService = plantService;
        this.robotModelAssembler = robotModelAssembler;
        this.growthCharModelAssembler = growthCharModelAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<GrowthCharResponse>> addRobotToPlant(Long plantId, Long robotId) {
        plantService.findById(plantId);
        GrowthCharResponse data = robotService.addRobotToPlant(plantId, robotId);
        EntityModel<GrowthCharResponse> model = growthCharModelAssembler.toModel(data);
        return ResponseEntity.accepted().body(model);
    }

    @Override
    public ResponseEntity<EntityModel<RobotResponse>> getRobotByID(Long robotId) {
        RobotResponse robot = robotService.findById(robotId);
        EntityModel<RobotResponse> model = robotModelAssembler.toModel(robot);
        return ResponseEntity.ok(model);
    }
}
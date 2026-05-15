package org.plrest.controllers;

import org.obs.dto.*;
import org.obs.endpoints.UserApi;
import org.plrest.service.PlantService;
import org.plrest.service.PlantSampleService;
import org.plrest.service.RobotService;
import org.plrest.assemblers.HomePlantModelAssembler;
import org.plrest.assemblers.PlantSampleModelAssembler;
import org.plrest.assemblers.RobotModelAssembler;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserApiController implements UserApi {

    private final PlantService plantService;
    private final PlantSampleService plantSampleService;
    private final RobotService robotService;
    private final HomePlantModelAssembler homePlantModelAssembler;
    private final PlantSampleModelAssembler plantSampleModelAssembler;
    private final RobotModelAssembler robotModelAssembler;
    private final PagedResourcesAssembler<HomePlantResponse> pagedHomePlantsAssembler;
    private final PagedResourcesAssembler<PlantsSampleResponse> pagedPlantSamplesAssembler;

    public UserApiController(PlantService plantService,
                             PlantSampleService plantSampleService,
                             RobotService robotService,
                             HomePlantModelAssembler homePlantModelAssembler,
                             PlantSampleModelAssembler plantSampleModelAssembler,
                             RobotModelAssembler robotModelAssembler,
                             PagedResourcesAssembler<HomePlantResponse> pagedHomePlantsAssembler,
                             PagedResourcesAssembler<PlantsSampleResponse> pagedPlantSamplesAssembler) {
        this.plantService = plantService;
        this.plantSampleService = plantSampleService;
        this.robotService = robotService;
        this.homePlantModelAssembler = homePlantModelAssembler;
        this.plantSampleModelAssembler = plantSampleModelAssembler;
        this.robotModelAssembler = robotModelAssembler;
        this.pagedHomePlantsAssembler = pagedHomePlantsAssembler;
        this.pagedPlantSamplesAssembler = pagedPlantSamplesAssembler;
    }

    @Override
    public EntityModel<GrowthCharResponse> getPlantCharacteristics(Long plantId) {
        // Возвращает характеристики растения (можно заменить на реальный сервис)
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PagedModel<EntityModel<HomePlantResponse>> getAllPlants(int page, int size) {
        Page<HomePlantResponse> paged = plantService.findAllByUser(page, size);
        return pagedHomePlantsAssembler.toModel(paged, homePlantModelAssembler);
    }

    @Override
    public EntityModel<HomePlantResponse> getPlantById(Long idPlant) {
        return homePlantModelAssembler.toModel(plantService.findById(idPlant));
    }

    @Override
    public ResponseEntity<EntityModel<HomePlantResponse>> createPlant(HomePlantRequest request) {
        HomePlantResponse created = plantService.create(request);
        EntityModel<HomePlantResponse> model = homePlantModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<HomePlantResponse> updatePlant(Long idPlant, HomePlantRequest request) {
        return homePlantModelAssembler.toModel(plantService.update(idPlant, request));
    }

    @Override
    public EntityModel<HomePlantResponse> patchPlant(Long idPlant, HomePlantRequest request) {
        return homePlantModelAssembler.toModel(plantService.patch(idPlant, request));
    }

    @Override
    public void deletePlant(Long idPlant) {
        plantService.delete(idPlant);
    }

    @Override
    public PagedModel<EntityModel<PlantsSampleResponse>> searchPlantSamplesByType(String type, int page, int size) {
        Page<PlantsSampleResponse> paged = plantSampleService.findByType(type, page, size);
        return pagedPlantSamplesAssembler.toModel(paged, plantSampleModelAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<RobotResponse>> addRobotToPlant(Long plantId, RobotRequest request) {
        plantService.findById(plantId);
        RobotResponse created = robotService.createAndBind(plantId, request);
        EntityModel<RobotResponse> model = robotModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public void removeRobotFromPlant(Long robotId, Long plantId) {
        robotService.unbind(robotId, plantId);
    }

    @Override
    public EntityModel<RobotResponse> replaceRobotData(Long plantId, Long robotId, RobotRequest request) {
        return robotModelAssembler.toModel(robotService.replace(plantId, robotId, request));
    }

    @Override
    public EntityModel<RobotResponse> changeRobotData(Long plantId, Long robotId, RobotRequest request) {
        return robotModelAssembler.toModel(robotService.patch(plantId, robotId, request));
    }
}
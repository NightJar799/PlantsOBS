package org.plrest.assemblers;

import org.plrest.controllers.PlantManagerApiController;
import org.plrest.controllers.RobotApiController;
import org.plrest.controllers.UserApiController;
import org.obs.dto.RobotResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class RobotModelAssembler implements RepresentationModelAssembler<RobotResponse, EntityModel<RobotResponse>> {

    @Override
    public EntityModel<RobotResponse> toModel(RobotResponse robot) {
        EntityModel<RobotResponse> model = EntityModel.of(robot,
            linkTo(methodOn(RobotApiController.class).
                getRobotByID(robot.getId())).withSelfRel(),
            linkTo(methodOn(UserApiController.class).
                patchRobotData(robot.getId(), robot.getPlantId(), null)).withRel("replace"),
            linkTo(methodOn(UserApiController.class).
                putRobotData(robot.getId(), robot.getPlantId(), null)).withRel("patch"),
            linkTo(methodOn(UserApiController.class).
                removeRobotFromPlant(robot.getId(), robot.getPlantId())).withRel("unbind"),
            linkTo(methodOn(PlantManagerApiController.class).
                getPlantEnvironmentData(robot.getPlantId())).withRel("robotsDataOfPlant"),
            linkTo(methodOn( UserApiController.class).
                addRobotToPlant(null, RobotResponse.map(robot))).withRel("addrobotToPlant")
        );

        return model;
    }
}
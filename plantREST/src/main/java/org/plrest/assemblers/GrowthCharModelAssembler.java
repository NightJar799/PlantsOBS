package org.plrest.assemblers;

import org.plrest.controllers.PlantManagerApiController;
import org.plrest.controllers.RobotApiController;
import org.plrest.controllers.UserApiController;
import org.obs.dto.GrowthCharRequest;
import org.obs.dto.GrowthCharResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class GrowthCharModelAssembler implements RepresentationModelAssembler<GrowthCharResponse, EntityModel<GrowthCharResponse>> {

    @Override
    public EntityModel<GrowthCharResponse> toModel(GrowthCharResponse growthChar) {
        return EntityModel.of(growthChar,
                linkTo(methodOn(PlantManagerApiController.class).
                    submitPlantReport(growthChar.getId(), GrowthCharResponse.map(growthChar))).withRel("submit"),
                linkTo(methodOn(RobotApiController.class).
                    sendDataToPlant(growthChar.getId(), null)).withRel("robotSubmits"),
                linkTo(methodOn(UserApiController.class).
                    getPlantCharacteristics(growthChar.getId())).withSelfRel()
        );
    }
}
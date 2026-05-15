package org.plrest.assemblers;

import org.plrest.controllers.PlantManagerApiController;
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
                linkTo(methodOn(PlantManagerApiController.class).getPlantCharacteristics(growthChar.getId())).withSelfRel(),
                linkTo(methodOn(PlantManagerApiController.class).getPlantEnvironmentData(growthChar.getId(), 0)).withRel("environment"),
                linkTo(methodOn(PlantManagerApiController.class).submitPlantReport(growthChar.getId(), null)).withRel("submit")
        );
    }
}
package org.plrest.assemblers;

import org.plrest.controllers.AdminApiController;
import org.plrest.controllers.UserApiController;
import org.obs.dto.HomePlantResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class HomePlantModelAssembler implements RepresentationModelAssembler<HomePlantResponse, EntityModel<HomePlantResponse>> {

    @Override
    public EntityModel<HomePlantResponse> toModel(HomePlantResponse plant) {
        EntityModel<HomePlantResponse> model = EntityModel.of(plant,
                linkTo(methodOn(UserApiController.class).
                    getPlantById(plant.getId())).withSelfRel(),
                linkTo(methodOn(UserApiController.class).
                    updatePlant(plant.getId(), null)).withRel("update"),
                linkTo(methodOn(UserApiController.class).
                    patchPlant(plant.getId(), null)).withRel("patch"),
                linkTo(methodOn(UserApiController.class).
                    deletePlant(plant.getId())).withRel("delete")
        );

        if (plant.getId() != null) {
            model.add(linkTo(methodOn(AdminApiController.class).getPlantSampleById(plant.getId())).withRel("sample"));
        }

        return model;
    }
}
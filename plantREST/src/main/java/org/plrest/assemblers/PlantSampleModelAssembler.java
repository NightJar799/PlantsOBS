package org.plrest.assemblers;

import org.plrest.controllers.AdminApiController;
import org.obs.dto.PlantsSampleResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PlantSampleModelAssembler implements RepresentationModelAssembler<PlantsSampleResponse, EntityModel<PlantsSampleResponse>> {

    @Override
    public EntityModel<PlantsSampleResponse> toModel(PlantsSampleResponse sample) {
        return EntityModel.of(sample,
                linkTo(methodOn(AdminApiController.class).getPlantSampleById(sample.getId())).withSelfRel(),
                linkTo(methodOn(AdminApiController.class).getAllPlantSamples(0, 20)).withRel("collection"),
                linkTo(methodOn(AdminApiController.class).putSample(sample.getId(), null)).withRel("update"),
                linkTo(methodOn(AdminApiController.class).patchSample(sample.getId(), null)).withRel("patch"),
                linkTo(methodOn(AdminApiController.class).deleteSample(sample.getId(), null)).withRel("delete")
        );
    }
}
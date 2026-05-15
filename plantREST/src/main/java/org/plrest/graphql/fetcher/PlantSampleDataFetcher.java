package org.plrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.obs.dto.PlantSampleRequest;
import org.obs.dto.PlantsSampleResponse;
import org.plrest.graphql.types.PlantSampleConnectionGql;
import org.plrest.graphql.types.PageInfoGql;
import org.plrest.graphql.types.CreatePlantSampleInputGql;
import org.plrest.graphql.types.UpdatePlantSampleInputGql;
import org.plrest.graphql.types.PlantSampleFilterGql;
import org.plrest.service.PlantSampleService;
import org.springframework.data.domain.Page;

@DgsComponent
public class PlantSampleDataFetcher {

    private final PlantSampleService plantSampleService;

    public PlantSampleDataFetcher(PlantSampleService plantSampleService) {
        this.plantSampleService = plantSampleService;
    }

    @DgsQuery
    public PlantsSampleResponse plantSample(@InputArgument String id) {
        return plantSampleService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public PlantSampleConnectionGql plantSamples(
            @InputArgument PlantSampleFilterGql filter,
            @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Page<PlantsSampleResponse> paged;

        if (filter != null && filter.type() != null) {
            paged = plantSampleService.findByType(filter.type(), pageNum, pageSize);
        } else {
            paged = plantSampleService.findAll(pageNum, pageSize);
        }

        return new PlantSampleConnectionGql(
                paged.getContent(),
                new PageInfoGql(paged.getNumber(), paged.getSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements());
    }

    @DgsMutation
    public PlantsSampleResponse createPlantSample(@InputArgument CreatePlantSampleInputGql input) {
        PlantSampleRequest request = new PlantSampleRequest(
                null,
                input.type(),
                input.fruiting(),
                input.flower(),
                input.difficulty(),
                input.wikiUrl()
        );
        return plantSampleService.create(request);
    }

    @DgsMutation
    public PlantsSampleResponse updatePlantSample(
            @InputArgument String id,
            @InputArgument UpdatePlantSampleInputGql input) {
        PlantSampleRequest request = new PlantSampleRequest(
                Long.parseLong(id),
                input.type(),
                input.fruiting(),
                input.flower(),
                input.difficulty(),
                input.wikiUrl()
        );
        return plantSampleService.update(Long.parseLong(id), request);
    }

    @DgsMutation
    public boolean deletePlantSample(@InputArgument String id) {
        plantSampleService.delete(Long.parseLong(id));
        return true;
    }
}
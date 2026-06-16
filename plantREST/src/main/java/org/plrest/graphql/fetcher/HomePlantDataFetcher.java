package org.plrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.obs.dto.GrowthCharRequest;
import org.obs.dto.GrowthCharResponse;
import org.obs.dto.HomePlantRequest;
import org.obs.dto.HomePlantResponse;
import org.plrest.graphql.types.*;
import org.plrest.service.GrowthCharService;
import org.plrest.service.PlantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;

@DgsComponent
public class HomePlantDataFetcher {

    private final PlantService plantService;
    private final GrowthCharService growthCharService;

    private static final Logger log = LoggerFactory.getLogger(GrowthCharacteristicDataFetcher.class);

    public HomePlantDataFetcher(PlantService plantService, GrowthCharService growthCharService) {
        this.plantService = plantService;
        this.growthCharService = growthCharService;
    }

    @DgsQuery
    public HomePlantResponse homePlant(@InputArgument String id) {
        return plantService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public HomePlantConnectionGql homePlants(
            @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Page<HomePlantResponse> paged = plantService.findAllByUser(pageNum, pageSize);

        return new HomePlantConnectionGql(
                paged.getContent(),
                new PageInfoGql(paged.getNumber(), paged.getSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements());
    }

    @DgsMutation
    public HomePlantResponse createHomePlant(@InputArgument CreateHomePlantInputGql input) {
        HomePlantRequest request = new HomePlantRequest(
                null,  // id будет сгенерирован сервисом
                input.sampleId(),
                input.name(),
                input.note(),
                input.species(),
                input.age()
        );
        return plantService.create(request);
    }

    @DgsMutation
    public HomePlantResponse updateHomePlant(
            @InputArgument String id,
            @InputArgument UpdateHomePlantInputGql input) {
        HomePlantRequest request = new HomePlantRequest(
                Long.parseLong(id),
                null,
                input.name(),
                input.note(),
                input.species(),
                input.age()
        );
        return plantService.update(Long.parseLong(id), request);
    }

    @DgsMutation(field = "submitGrowthReportFromPlant")
    public GrowthCharResponse submitGrowthReport(
            @InputArgument String plantId,
            @InputArgument CreateGrowthCharInputGql input) {
        log.debug("Submitting growth report from plant: {}", plantId);
        return growthCharService.submitReport(
                Long.parseLong(plantId),
                toRequest(input)
        );
    }

    @DgsMutation
    public boolean deleteHomePlant(@InputArgument String id) {
        plantService.delete(Long.parseLong(id));
        return true;
    }

    @DgsMutation
    public HomePlantResponse linkHomePlantToSample(
            @InputArgument String plantId,
            @InputArgument String sampleId) {
        plantService.linkToSample(Long.parseLong(plantId), Long.parseLong(sampleId));
        return plantService.findById(Long.parseLong(plantId));
    }

    private GrowthCharRequest toRequest(CreateGrowthCharInputGql input) {
        return new GrowthCharRequest(
                null,
                null,
                input.lx(),
                input.water(),
                input.heat(),
                input.air(),
                input.nitrogen(),
                input.soilPh(),
                input.humidity(),
                input.recordedAt()
        );
    }
}
package org.plrest.graphql.fetcher;

import com.netflix.graphql.dgs.*;
import org.obs.dto.GrowthCharRequest;
import org.obs.dto.GrowthCharResponse;
import org.plrest.graphql.types.CreateGrowthCharInputGql;
import org.plrest.service.GrowthCharService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DgsComponent
public class GrowthCharacteristicDataFetcher {

    private static final Logger log = LoggerFactory.getLogger(GrowthCharacteristicDataFetcher.class);
    private final GrowthCharService growthCharService;

    public GrowthCharacteristicDataFetcher(GrowthCharService growthCharService) {
        this.growthCharService = growthCharService;
    }

    @DgsQuery(field = "growthCharacteristics")
    public GrowthCharResponse getGrowthCharacteristics(@InputArgument String plantId) {
        log.debug("Fetching growth characteristics for plant: {}", plantId);
        return growthCharService.findByPlantId(Long.parseLong(plantId));
    }

    @DgsMutation(field = "submitGrowthReportFromRobot")
    public GrowthCharResponse submitGrowthReportFromRobot(
            @InputArgument String plantId,
            @InputArgument String robotId,
            @InputArgument CreateGrowthCharInputGql input) {
        log.debug("Submitting growth report from robot: {} for plant: {}", robotId, plantId);
        return growthCharService.createFromRobot(
                Long.parseLong(plantId),
                Long.parseLong(robotId),
                toRequest(input)
        );
    }

    @DgsMutation(field = "sendRecommendations")
    public Boolean sendRecommendations(
            @InputArgument String plantId,
            @InputArgument CreateGrowthCharInputGql input) {
        log.debug("Sending recommendations for plant: {}", plantId);
        growthCharService.sendRecommendations(
                Long.parseLong(plantId),
                toRequest(input)
        );
        return true;
    }

    private GrowthCharRequest toRequest(CreateGrowthCharInputGql input) {
        return new GrowthCharRequest(
                null,
                input.lx(),
                input.water(),
                input.heat(),
                input.air(),
                input.nitrogen(),
                input.soilPh(),
                input.humidity()
        );
    }
}
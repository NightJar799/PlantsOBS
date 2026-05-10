package org.plrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import org.obs.dto.GrowthCharResponse;
import org.obs.dto.RobotRequest;
import org.obs.dto.RobotResponse;
import org.plrest.graphql.types.RobotConnectionGql;
import org.plrest.graphql.types.PageInfoGql;
import org.plrest.graphql.types.CreateRobotInputGql;
import org.plrest.graphql.types.UpdateRobotInputGql;
import org.plrest.service.RobotService;
import org.plrest.service.GrowthCharService;
import org.springframework.data.domain.Page;

@DgsComponent
public class RobotDataFetcher {

    private final RobotService robotService;
    private final GrowthCharService growthCharService;

    public RobotDataFetcher(RobotService robotService, GrowthCharService growthCharService) {
        this.robotService = robotService;
        this.growthCharService = growthCharService;
    }

    @DgsQuery
    public RobotResponse robot(@InputArgument String id) {
        return robotService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public RobotConnectionGql robotsByPlant(
            @InputArgument String plantId,
            @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        Page<RobotResponse> paged = robotService.findByPlantId(Long.parseLong(plantId), pageNum, pageSize);

        return new RobotConnectionGql(
                paged.getContent(),
                new PageInfoGql(paged.getNumber(), paged.getSize(), paged.getTotalPages(), paged.isLast()),
                (int) paged.getTotalElements());
    }

    @DgsMutation
    public RobotResponse createRobot(
            @InputArgument String plantId,
            @InputArgument CreateRobotInputGql input) {
        RobotRequest request = new RobotRequest(
                null,
                Long.parseLong(plantId),
                input.name(),
                input.sensorType(),
                input.measuredCharacteristic(),
                input.usedCharacteristic()
        );
        return robotService.createAndBind(Long.parseLong(plantId), request);
    }

    @DgsMutation
    public RobotResponse updateRobot(
            @InputArgument String plantId,
            @InputArgument String robotId,
            @InputArgument UpdateRobotInputGql input) {
        RobotRequest request = new RobotRequest(
                Long.parseLong(robotId),
                Long.parseLong(plantId),
                input.name(),
                input.sensorType(),
                input.measuredCharacteristic(),
                input.usedCharacteristic()
        );
        return robotService.replace(Long.parseLong(plantId), Long.parseLong(robotId), request);
    }

    @DgsMutation
    public boolean deleteRobot(
            @InputArgument String robotId,
            @InputArgument String plantId) {
        robotService.unbind(Long.parseLong(robotId), Long.parseLong(plantId));
        return true;
    }

    @DgsMutation
    public GrowthCharResponse addRobotToPlant(
            @InputArgument String plantId,
            @InputArgument String robotId) {
        return robotService.addRobotToPlant(Long.parseLong(plantId), Long.parseLong(robotId));
    }

    @DgsMutation
    public boolean removeRobotFromPlant(
            @InputArgument String robotId,
            @InputArgument String plantId) {
        robotService.unbind(Long.parseLong(robotId), Long.parseLong(plantId));
        return true;
    }
}
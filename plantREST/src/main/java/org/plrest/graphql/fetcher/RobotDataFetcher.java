package org.plrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;

import java.util.Collections;
import java.util.List;

import org.obs.dto.GrowthCharResponse;
import org.obs.dto.RobotRequest;
import org.obs.dto.RobotResponse;
import org.plrest.graphql.types.RobotConnectionGql;
import org.plrest.graphql.types.PageInfoGql;
import org.plrest.graphql.types.CreateRobotInputGql;
import org.plrest.graphql.types.UpdateRobotInputGql;
import org.plrest.service.RobotService;
import org.springframework.data.domain.Page;

@DgsComponent
public class RobotDataFetcher {

    private final RobotService robotService;

    public RobotDataFetcher(RobotService robotService) {
        this.robotService = robotService;
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

    List<RobotResponse> allRobots = robotService.findByPlantId(Long.parseLong(plantId));

    int start = pageNum * pageSize;
    int end = Math.min(start + pageSize, allRobots.size());
    boolean hasNext = end < allRobots.size();

    List<RobotResponse> pagedContent = (start < allRobots.size()) 
            ? allRobots.subList(start, end) 
            : Collections.emptyList();

    int totalPages = (int) Math.ceil((double) allRobots.size() / pageSize);
    boolean isLast = (pageNum >= totalPages - 1) || allRobots.isEmpty();

    PageInfoGql pageInfo = new PageInfoGql(pageNum, pageSize, totalPages, isLast);
    return new RobotConnectionGql(pagedContent, pageInfo, allRobots.size());
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
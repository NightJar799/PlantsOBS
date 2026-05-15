package org.plrest.graphql.types;

import org.obs.dto.RobotResponse;
import java.util.List;

public record RobotConnectionGql(
        List<RobotResponse> content,
        PageInfoGql pageInfo,
        int totalElements
) {}
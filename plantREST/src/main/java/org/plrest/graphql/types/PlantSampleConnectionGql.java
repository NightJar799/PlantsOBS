package org.plrest.graphql.types;

import org.obs.dto.PlantsSampleResponse;
import java.util.List;

public record PlantSampleConnectionGql(
        List<PlantsSampleResponse> content,
        PageInfoGql pageInfo,
        int totalElements
) {}
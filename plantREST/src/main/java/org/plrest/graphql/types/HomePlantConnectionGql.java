package org.plrest.graphql.types;

import org.obs.dto.HomePlantResponse;
import java.util.List;

public record HomePlantConnectionGql(
        List<HomePlantResponse> content,
        PageInfoGql pageInfo,
        int totalElements
) {}
package org.plrest.graphql.types;

import java.time.Instant;
import java.time.OffsetDateTime;

public record CreateGrowthCharInputGql(
        Integer lx,
        Integer water,
        Integer heat,
        Integer air,
        Integer nitrogen,
        Double soilPh,
        String humidity,
        OffsetDateTime recordedAt
) {}
package org.plrest.graphql.types;

public record PlantSampleFilterGql(
        String type,
        Integer difficulty,
        Boolean hasFlower
) {}
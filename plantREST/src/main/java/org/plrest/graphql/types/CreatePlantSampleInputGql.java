package org.plrest.graphql.types;

public record CreatePlantSampleInputGql(
        String type,
        String fruiting,
        String flower,
        Integer difficulty,
        String wikiUrl
) {}
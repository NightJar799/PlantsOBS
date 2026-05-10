package org.plrest.graphql.types;

public record UpdatePlantSampleInputGql(
        String type,
        String fruiting,
        String flower,
        Integer difficulty,
        String wikiUrl
) {}
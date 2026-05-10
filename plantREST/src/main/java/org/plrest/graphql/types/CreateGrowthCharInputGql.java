package org.plrest.graphql.types;

public record CreateGrowthCharInputGql(
        Integer lx,
        Integer water,
        Integer heat,
        Integer air,
        Integer nitrogen,
        Double soilPh,
        String humidity
) {}
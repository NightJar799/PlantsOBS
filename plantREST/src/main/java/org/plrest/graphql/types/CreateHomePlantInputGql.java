package org.plrest.graphql.types;

public record CreateHomePlantInputGql(
        String name,
        String note,
        String species,
        Integer age,
        Long sampleId
) {}
package org.plrest.graphql.types;

public record UpdateHomePlantInputGql(
        String name,
        String note,
        String species,
        Integer age
) {}
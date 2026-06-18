package org.plantrmq;

public sealed interface HomePlantEvent {

    record Created(
            Long plantId,
            String name,
            String note,
            String species,
            Integer age,
            Long plantSampleId
    ) implements HomePlantEvent {}

    record Updated(
            Long plantId,
            String name,
            String note,
            String species,
            Integer age,
            Long plantSampleId
    ) implements HomePlantEvent {}

    record Deleted(
            Long plantId,
            String name
    ) implements HomePlantEvent {}
}

package org.plantrmq.events;

public sealed interface HomePlantEvent {

    record Created(
            Long plantId,
            String name,
            String note,
            String species,
            Integer age,
            Long plantSampleId,
            Long userId
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

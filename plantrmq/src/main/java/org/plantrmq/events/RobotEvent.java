package org.plantrmq.events;

public sealed interface RobotEvent {

    record Created(
            Long robotId,
            String name,
            String sensorType,
            String measuredCharacteristic,
            String usedCharacteristics,
            Long plantId
    ) implements RobotEvent {}

    record Updated(
            Long robotId,
            String name,
            String sensorType,
            String measuredCharacteristic,
            String usedCharacteristics
    ) implements RobotEvent {}

    record Deleted(
            Long robotId,
            String name
    ) implements RobotEvent {}
}

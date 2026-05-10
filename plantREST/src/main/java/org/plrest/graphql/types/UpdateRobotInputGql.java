package org.plrest.graphql.types;

public record UpdateRobotInputGql(
        String name,
        Integer sensorType,
        String measuredCharacteristic,
        String usedCharacteristic
) {}
package org.plrest.graphql.types;

public record CreateRobotInputGql(
        String name,
        Integer sensorType,
        String measuredCharacteristic,
        String usedCharacteristic
) {}
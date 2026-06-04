package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "robots", itemRelation = "robot")
@Schema(description = "Информация о роботе (ответ API)")
public class RobotResponse extends RepresentationModel<RobotResponse> {
    public static RobotRequest map(RobotResponse response) {
        return new RobotRequest(response.getId(), response.getPlantId(), response.getName(),
             response.getSensorType(), response.getMeasuredCharacteristic(), response.getUsedCharacteristic());
    }

    @Schema(description = "Уникальный идентификатор робота", example = "1")
    private final Long id;

    @Schema(description = "Уникальный идентификатор растения датчика", example = "1")
    private final Long plantId;

    @Schema(description = "Имя робота", example = "Сенсор-1")
    private final String name;

    @Schema(description = "Тип датчика", example = "2")
    private final Integer sensorType;

    @Schema(description = "Измеряемая характеристика", example = "Температура")
    private final String measuredCharacteristic;

    @Schema(description = "Используемые измеряемая характеристика", example = "Температура", requiredMode = Schema.RequiredMode.REQUIRED)
    private final String usedCharacteristic;
}

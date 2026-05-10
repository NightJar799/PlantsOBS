package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание или полное обновление робота (все поля обязательны)")
public record RobotRequest(

        @Schema(description = "Уникальный идентификатор датчика", example = "1")
        @NotNull(message = "Индифактор у датчика должен быть")
        Long id,

        @Schema(description = "Уникальный идентификатор растения датчика", example = "1")
        Long plantId,

        @Schema(description = "Имя робота", example = "Сенсор-1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Имя не может быть пустым")
        @Size(max = 100, message = "Имя не может превышать 100 символов")
        String name,

        @Schema(description = "Количество отдваваемых характеристик", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Больше одной харакетристики")
        Integer sensorType,

        @Schema(description = "Измеряемая характеристика", example = "Температура", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Характеристика не может быть пустой")
        @Size(max = 100, message = "Характеристика не может превышать 100 символов")
        String measuredCharacteristic,

        @Schema(description = "Используемые измеряемая характеристика", example = "Температура", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(max = 100, message = "Используемые арактеристики не могут превышать 100 символов")
        String usedCharacteristic
) {
}
package org.obs.dto;

import java.time.Instant;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание/обновление параметров роста (все поля обязательны)")
public record GrowthCharRequest(

        @Schema(description = "Уникальный идентификатор примера био-характеристик", example = "1")
        @NotNull(message = "Индифактор у био-характеристик растения должен быть")
        Long id,

        @Schema(description = "Уникальный идентификатор растения с которым связана данная запись", example = "1")
        @NotNull(message = "У био-характеристики растения должено быть растение, которому оно принадлежит")
        Long homePlantId,

        @Schema(description = "Свет (в люксах)", example = "5000", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Освещение не может быть пустым")
        @Min(value = 0, message = "Освещение не может быть отрицательным")
        Integer lx,

        @Schema(description = "Влажность почвы (в процентах)", example = "80", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Влажность не может быть пустой")
        @Min(value = 0, message = "Влажность должна быть от 0 до 100")
        @Max(value = 100, message = "Влажность должна быть от 0 до 100")
        Integer water,

        @Schema(description = "Температура (градусы Цельсия)", example = "22", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Температура не может быть пустой")
        Integer heat,

        @Schema(description = "Уровень CO2 (воздух)", example = "400", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Уровень CO2 не может быть пустым")
        Integer air,

        @Schema(description = "Уровень азота в почве", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Уровень азота не может быть пустым")
        Integer nitrogen,

        @Schema(description = "Кислотность почвы (pH)", example = "6.5", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Кислотность не может быть пустой")
        @Min(value = 0, message = "pH должен быть в диапазоне 0-14")
        @Max(value = 14, message = "pH должен быть в диапазоне 0-14")
        Double soilPh,

        @Schema(description = "Влажность", example = "50%", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 7, message = "Число во влажности не может превышать 7 символов")
        String humidity,

        @Schema(description = "Время создания", example = "2026-06-14T15:49:00.123456789Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        OffsetDateTime recordedAt
) {
}
package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание домашнего растения (все поля обязательны)")
public record HomePlantRequest(

        @Schema(description = "Уникальный идентификатор растения", example = "1")
        @NotNull(message = "Индифактор у растения должен быть")
        Long id,

        @Schema(description = "Уникальный идентификатор примера растения", example = "1")
        Long sampleId,

        @Schema(description = "Имя, заданное пользователем", example = "Мой фикус", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Имя не может быть пустым")
        @Size(max = 100, message = "Имя не может превышать 100 символов")
        String name,

        @Schema(description = "Примечание пользователя", example = "Поливать раз в неделю", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Примечание не может быть пустым")
        @Size(max = 500, message = "Примечание не может превышать 500 символов")
        String note,

        @Schema(description = "Научное название вида", example = "Ficus elastica", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Вид не может быть пустым")
        @Size(max = 200, message = "Вид не может превышать 200 символов")
        String species,

        @Schema(description = "Возраст растения (в годах)", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Возраст не может быть пустым")
        @Min(value = 0, message = "Возраст не может быть отрицательным")
        Integer age
) {
}
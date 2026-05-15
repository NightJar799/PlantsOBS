package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания или полного обновления шаблона растения (POST / PUT).
 * Все обязательные поля должны присутствовать.
 */
@Schema(description = "Запрос на создание или полное обновление шаблона")
public record PlantSampleRequest(

        @Schema(description = "Уникальный идентификатор примера растения", example = "1")
        @NotNull(message = "Индифактор у примера растения должен быть")
        Long id,

        @Schema(description = "Тип", example = "Хвойное", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 100, message = "Тип не может превышать 100 символов")
        String type,

        @Schema(description = "Плодоношение", example = "Да", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 10, message = "Плодоношение не может превышать 10 символов")
        String fruiting,

        @Schema(description = "Цветок", example = "Да", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 10, message = "Цвет не может превышать 10 символов")
        String flower,

        @Schema(description = "Сложность выращивания", example = "5" , requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @Size(max = 10, message = "Число сложности от 1 до 10")
        Integer difficulty,

        @Schema(description = "Ссылка на Wikipedia", example = "https://ru.wikipedia.org/wiki/Сосна")
        @Size(max = 50, message = "Ссылка не может быть длинеее 50 символов")
        String wikiUrl
) {}
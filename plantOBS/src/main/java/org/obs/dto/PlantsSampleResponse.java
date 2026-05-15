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
@Relation(collectionRelation = "plantSamples", itemRelation = "plantSample")
@Schema(description = "Информация о шаблоне растения (справочная информация)")
public class PlantsSampleResponse extends RepresentationModel<PlantsSampleResponse> {

    @Schema(description = "Уникальный идентификатор шаблона", example = "1")
    private final Long id;

    @Schema(description = "Тип растения", example = "Хвойное")
    private final String type;

    @Schema(description = "Плодоношение", example = "Да", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String fruiting;

    @Schema(description = "Наличие цветка", example = "Да")
    private final String flower;

    @Schema(description = "Сложность выращивания (1-10)", example = "5")
    private final Integer difficulty;

    @Schema(description = "Ссылка на Wikipedia", example = "https://ru.wikipedia.org/wiki/Сосна")
    private final String wikiUrl;
}
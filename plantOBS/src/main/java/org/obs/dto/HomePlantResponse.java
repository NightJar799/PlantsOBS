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
@Relation(collectionRelation = "homePlants", itemRelation = "homePlant")
@Schema(description = "Информация о домашнем растении (ответ API)")
public class HomePlantResponse extends RepresentationModel<HomePlantResponse> {

    @Schema(description = "Уникальный идентификатор растения", example = "1")
    private final Long id;

    @Schema(description = "Уникальный идентификатор примера растения", example = "1")
    private final Long sampleId;

    @Schema(description = "Имя, заданное пользователем", example = "Мой фикус")
    private final String name;

    @Schema(description = "Примечание пользователя", example = "Поливать раз в неделю")
    private final String note;

    @Schema(description = "Научное название вида", example = "Ficus elastica")
    private final String species;

    @Schema(description = "Возраст растения (в годах)", example = "2")
    private final Integer age;
}
package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "growthChars", itemRelation = "growthChar")
@Schema(description = "Информация о параметрах роста (ответ API)")
public class GrowthCharResponse extends RepresentationModel<GrowthCharResponse> {

    public static GrowthCharRequest map(GrowthCharResponse response) {
        return new GrowthCharRequest(response.id, response.homePlantId, response.lx, response.water, response.heat,
             response.air, response.nitrogen, response.soilPh, response.humidity, response.recordedAt);
    }

    @Schema(description = "Уникальный идентификатор примера био-характеристик", example = "1")
    private final Long id;

    @Schema(description = "Уникальный идентификатор растения с которым связана данная запись", example = "1")
    private final Long homePlantId;

    @Schema(description = "Свет (в люксах)", example = "5000")
    private final Integer lx;

    @Schema(description = "Влажность почвы (в процентах)", example = "80")
    private final Integer water;

    @Schema(description = "Температура (градусы Цельсия)", example = "22")
    private final Integer heat;

    @Schema(description = "Уровень CO2 (воздух)", example = "400")
    private final Integer air;

    @Schema(description = "Уровень азота в почве", example = "30")
    private final Integer nitrogen;

    @Schema(description = "Кислотность почвы (pH)", example = "6.5")
    private final Double soilPh;

    @Schema(description = "Влажность", example = "50%")
    String humidity;

    @Schema(description = "Время создания", example = "2026-06-14T15:49:00.123456789Z", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    OffsetDateTime recordedAt;
}
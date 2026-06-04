package org.obs.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.obs.config.PlantsApiContractConfig;
import org.obs.dto.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контракт API для управления домашними растениями.
 */
@Tag(name = "Plant Manager", description = "Управление домашними растениями")
@RequestMapping(
        value = "/api/plants",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface PlantManagerApi {
    @Operation(
            summary = "Получить данные о датчиках растения",
            description = "Возвращает информацию о датчиках, привязанных к растению, и их показаниях",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Данные получены")
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{plantId}/environment")
    CollectionModel<RobotResponse> getPlantEnvironmentData(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId);

    @Operation(
            summary = "Отправить отчёт о состоянии растения",
            description = "Позволяет отправить актуальные показатели роста растения пользователю",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Отчёт принят")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/{plantId}/report", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<GrowthCharResponse>> submitPlantReport(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId,
            @Valid @RequestBody GrowthCharRequest request
    );

    @Operation(
            summary = "Получить рекомендации по растению",
            description = "Позволяет отправить рекомендации по уходу на основе текущих показателей",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "202", description = "Рекомендации приняты к обработке")
    @GetMapping(value = "/{plantId}/recommendations")
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<Void> getRecommendations(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId);
}
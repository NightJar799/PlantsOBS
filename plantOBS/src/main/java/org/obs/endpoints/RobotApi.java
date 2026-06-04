package org.obs.endpoints;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.obs.config.PlantsApiContractConfig;
import org.obs.dto.ErrorResponse;
import org.obs.dto.GrowthCharResponse;
import org.obs.dto.RobotResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контракт API для управления датчиками (роботами).
 * Позволяет добавлять и удалять датчики у растений.
 */
@Tag(name = "Robot", description = "Управление датчиками растений")
@RequestMapping(
        value = "/api/robot",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface RobotApi {

    @Operation(
            summary = "Отправить данные растению",
            description = "Отправялет все снимаемые данным датчиком даннеы о состоянии растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Снятые данные о растении успешно отправлены")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/{plantId}/{robotId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<EntityModel<GrowthCharResponse>> sendDataToPlant(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId,
            @Parameter(description = "ID датчика", required = true, example = "1") @PathVariable Long robotId
    );

    @Operation(
            summary = "Получить данные о датчике по id",
            description = "Получить полные даннеы о датчике растения по id",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Данные о датчике успешно отправлены")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping(value = "/{robotId}")
    @ResponseStatus(HttpStatus.FOUND)
    ResponseEntity<EntityModel<RobotResponse>> getRobotByID(
            @Parameter(description = "ID датчик", required = true, example = "1") @PathVariable Long robotId
    );
}
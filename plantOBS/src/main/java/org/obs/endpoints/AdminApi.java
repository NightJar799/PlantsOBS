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
import org.obs.dto.PlantSampleRequest;
import org.obs.dto.PlantsSampleResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.obs.dto.ErrorResponse;

@Tag(name = "Admins", description = "Управление мета данными шаблонов растений и общих настроек сервиса")
@RequestMapping(
        value = "/api/admin",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface AdminApi {

    @Operation(
            summary = "Получить список шаблонов растений",
            description = "Возвращает постраничный список доступных шаблонов растений (справочная информация)",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список шаблонов")
    @GetMapping(value = "samples")
    PagedModel<EntityModel<PlantsSampleResponse>> getAllPlantSamples(
            @Parameter(description = "Номер страницы (0..N)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "Получить шаблон растения по ID",
            description = "Возвращает подробную информацию о конкретном шаблоне растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Шаблон найден")
    @ApiResponse(responseCode = "404", description = "Шаблон не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("samples/{id}")
    EntityModel<PlantsSampleResponse> getPlantSampleById(
            @Parameter(description = "ID шаблона", required = true, example = "1") @PathVariable Long id
    );

    @Operation(
            summary = "Создать шаблон растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Шаблон создан. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "samples", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<PlantsSampleResponse>> createSample(
                @Valid @RequestBody PlantSampleRequest request);

    @Operation(
            summary = "Удалить шаблон растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Шаблон удалён. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping(value = "samples/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<PlantsSampleResponse>> deleteSample(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long id);

    @Operation(
            summary = "Изменить шаблон растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Шаблон изменён. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "samples/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<PlantsSampleResponse>> patchSample(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long id,
                                                                @Valid @RequestBody PlantSampleRequest request);

    @Operation(
            summary = "Полностью изменить шаблон растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Шаблон полностью изменён. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "samples/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<PlantsSampleResponse>> putSample(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long id,
                                                              @Valid @RequestBody PlantSampleRequest request);

}

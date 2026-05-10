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
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контракт API для пользователя.
 * Предоставляет доступ к шаблонам растений (справочная информация).
 */
@Tag(name = "User", description = "Пользовательские операции - просмотр шаблонов растений")
@RequestMapping(
        value = "/api/user/myPlants",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface UserApi {
    @Operation(
            summary = "Получить характеристики растения со ссылкой на Wiki",
            description = "Возвращает параметры роста растения с дополнительной ссылкой на Wiki",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Характеристики получены")
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{plantId}")
    EntityModel<GrowthCharResponse> getPlantCharacteristics(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId
    );

    @Operation(
            summary = "Получить список растений",
            description = "Возвращает постраничный список всех домашних растений пользователя",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список растений")
    @GetMapping
    PagedModel<EntityModel<HomePlantResponse>> getAllPlants(
            @Parameter(description = "Номер страницы (0..N)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "Получить растение по ID",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Растение найдено")
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{idPlant}")
    EntityModel<HomePlantResponse> getPlantById(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long idPlant
    );

    @Operation(
            summary = "Добавить новое растение",
            description = "Создаёт новую запись о домашнем растении",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Растение создано")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<HomePlantResponse>> createPlant(@Valid @RequestBody HomePlantRequest request);

    @Operation(
            summary = "Изменить растение (полное обновление)",
            description = "Заменяет все поля растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Растение обновлено")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/{idPlant}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<HomePlantResponse> updatePlant(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long idPlant,
            @Valid @RequestBody HomePlantRequest request
    );

    @Operation(
            summary = "Частичное обновление растения",
            description = "Обновляет только переданные поля (имя, примечание и т.д.)",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Растение обновлено")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{idPlant}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<HomePlantResponse> patchPlant(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long idPlant,
            @Valid @RequestBody HomePlantRequest request
    );

    @Operation(
            summary = "Удалить растение",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "204", description = "Растение удалено")
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{idPlant}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePlant(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long idPlant
    );

    @Operation(
            summary = "Поиск шаблонов по типу",
            description = "Возвращает шаблоны растений, отфильтрованные по типу (сосна, черника и т.д.)",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Результаты поиска")
    @GetMapping("/search")
    PagedModel<EntityModel<PlantsSampleResponse>> searchPlantSamplesByType(
            @Parameter(description = "Тип растения", required = true, example = "Клубника") @RequestParam String type,
            @Parameter(description = "Номер страницы (0..N)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "Добавить датчик растению",
            description = "Создаёт новый датчик (робота) и привязывает его к указанному растению",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Датчик создан и привязан к растению")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Растение не найдено",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(value = "/{plantId}/robots", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<RobotResponse>> addRobotToPlant(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId,
            @Valid @RequestBody RobotRequest request
    );

    @Operation(
            summary = "Удалить датчик растения",
            description = "Удаляет датчик и отвязывает его от растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "204", description = "Датчик удалён")
    @ApiResponse(responseCode = "404", description = "Датчик или растение не найдены",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{plantId}/robots")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeRobotFromPlant(
            @Parameter(description = "ID датчика", required = true, example = "10") @PathVariable Long robotId,
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId
    );

    @Operation(
            summary = "Полное обновление данных датчика",
            description = "Заменяет все показатели датчика для растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Данные обновлены")
    @PutMapping(value = "/{plantId}/robots", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<RobotResponse> replaceRobotData(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId,
            @Parameter(description = "ID датчика", required = true, example = "10") @PathVariable Long robotId,
            @Valid @RequestBody RobotRequest request
    );

    @Operation(
            summary = "Частичное обновление данных датчика",
            description = "Заменяет показатели датчика для растения",
            security = @SecurityRequirement(name = PlantsApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Данные обновлены")
    @PatchMapping(value = "/{plantId}/robots", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<RobotResponse> changeRobotData(
            @Parameter(description = "ID растения", required = true, example = "1") @PathVariable Long plantId,
            @Parameter(description = "ID датчика", required = true, example = "10") @PathVariable Long robotId,
            @Valid @RequestBody RobotRequest request
    );
}
package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание или полное обновление пользовтаеля (все поля обязательны)")
public record UserRequest(

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    @NotNull(message = "У пользоателя должен быть индентификатор")
    Long id,

    @Schema(description = "Имя пользователя", example = "Васька")
    @NotNull(message = "У пользоателя должно быть имя")
    String name,

    @Schema(description = "Почта", example = "Vasyska@yandex.ru")
    @NotNull(message = "У пользоателя должна быть почта")
    String email,

    @Schema(description = "Пароль", example = "123456789")
    @NotNull(message = "У пользоателя должен быть пароль")
    @Size(message = "у пароля должно быть хотя бы 5 символов", min = 5)
    String password,

    @Schema(description = "телефон", example = "+88005553535")
    @NotNull(message = "У пользоателя должен быть номер телефона")
    @Size(message = "у телефона должен быть размер не более 15 символов", max = 15)
    String phone

) {} 
package org.obs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@Relation(collectionRelation = "users", itemRelation = "user")
@Schema(description = "Информация о пользователе (ответ API)")
public class UserResponse {
    
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private final Long id;

    @Schema(description = "Имя пользователя", example = "Васька")
    private final String name;

    @Schema(description = "Почта", example = "Vasyska@yandex.ru")
    private final String email;

    @Schema(description = "Пароль", example = "123456789")
    private final String password;

    @Schema(description = "телефон", example = "+88005553535")
    private final String phone;
}

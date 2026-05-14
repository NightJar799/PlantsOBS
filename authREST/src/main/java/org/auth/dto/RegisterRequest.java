package org.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Имя пользователя необходим")
        @Size(min = 3, max = 50, message = "Имя пользователя должна быть от 3 до 50 символов")
        String username,
        
        @NotBlank(message = "Почта необходимо")
        @Email(message = "Почта должна быть валидна")
        String email,
        
        @NotBlank(message = "Пароль необходим")
        @Size(min = 6, message = "Пароль длинной должен быть хотя бы 6 символов")
        String password,
        
        @Size(max = 20, message = "Максимальная длина телефона 20 символов")
        String phoneNumber
) {}
package org.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Имя пользователя должно быть") 
        String username,
        
        @NotBlank(message = "Пароль должен быть") 
        String password
) {}
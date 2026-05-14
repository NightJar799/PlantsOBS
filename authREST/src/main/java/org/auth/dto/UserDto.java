package org.auth.dto;

public record UserDto(
        Long id,
        String username,
        String email,
        String phoneNumber,
        String role
) {}
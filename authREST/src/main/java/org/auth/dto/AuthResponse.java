package org.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,
        
        @JsonProperty("refresh_token")
        String refreshToken,
        
        @JsonProperty("token_type")
        String tokenType,
        
        @JsonProperty("expires_in")
        long expiresIn,
        
        UserDto user
) {
    public AuthResponse(String accessToken, String refreshToken, long expiresIn, UserDto user) {
        this(accessToken, refreshToken, "Bearer", expiresIn, user);
    }
}
package org.auth.controller;

import jakarta.validation.Valid;
import org.auth.dto.AuthRequest;
import org.auth.dto.AuthResponse;
import org.auth.dto.RegisterRequest;
import org.auth.dto.UserDto;
import org.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7);
        AuthResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<UserDto> validate(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        UserDto user = authService.validateToken(jwt);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
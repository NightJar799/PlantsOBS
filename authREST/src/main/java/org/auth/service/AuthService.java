package org.auth.service;

import org.auth.dto.*;
import org.auth.model.User;
import org.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EventPublisherService eventPublisher;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       EventPublisherService eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) throw new RuntimeException("Пользователь уже существует");
        if (userRepository.existsByEmail(request.email())) throw new RuntimeException("Почта уже используеться");

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .role(User.Role.USER)
                .build();

        user = userRepository.save(user);

        eventPublisher.publishUserCreatedEvent(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken, 
            jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis(),
            toUserDto(user)
        );
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        eventPublisher.publishUserLoginEvent(user);

        return new AuthResponse(accessToken, refreshToken,
                jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis(),
                toUserDto(user)
        );
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) throw new RuntimeException("Ошибка токена");

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(newAccessToken, newRefreshToken,
                jwtService.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis(),
                toUserDto(user)
        );
    }

    @Transactional(readOnly = true)
    public UserDto validateToken(String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        if (!jwtService.isTokenValid(token, user)) throw new RuntimeException("Ошибка токена");

        return toUserDto(user);
    }

    private UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getPhoneNumber(), user.getRole().name());
    }
}
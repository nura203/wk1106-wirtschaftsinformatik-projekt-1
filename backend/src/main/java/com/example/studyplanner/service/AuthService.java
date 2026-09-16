package com.example.studyplanner.service;

import com.example.studyplanner.dto.ChangePasswordRequest;
import com.example.studyplanner.dto.LoginRequest;
import com.example.studyplanner.dto.RegisterRequest;
import com.example.studyplanner.entity.User;
import com.example.studyplanner.repository.UserRepository;
import com.example.studyplanner.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (request.username() == null || request.username().isBlank()) {
            throw new IllegalArgumentException(
                    "Benutzername darf nicht leer sein"
            );
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException(
                    "E-Mail darf nicht leer sein"
            );
        }

        if (request.password() == null || request.password().length() < 8) {
            throw new IllegalArgumentException(
                    "Passwort muss mindestens 8 Zeichen lang sein"
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "E-Mail-Adresse ist bereits registriert"
            );
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(
                savedUser.getId()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getUsername()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "E-Mail oder Passwort ist falsch"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "E-Mail oder Passwort ist falsch"
            );
        }

        String token = jwtService.generateToken(
                user.getId()
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername()
        );
    }

    @Transactional
    public void changePassword(
            UUID userId,
            ChangePasswordRequest request
    ) {
        if (request == null
                || request.password() == null
                || request.password().length() < 8) {

            throw new IllegalArgumentException(
                    "Passwort muss mindestens 8 Zeichen lang sein"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Benutzer nicht gefunden"
                        )
                );

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        userRepository.save(user);
    }

    public record AuthResponse(
            String token,
            UUID userId,
            String username
    ) {
    }
}
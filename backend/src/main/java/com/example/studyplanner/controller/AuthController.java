package com.example.studyplanner.controller;

import com.example.studyplanner.dto.ChangePasswordRequest;
import com.example.studyplanner.dto.LoginRequest;
import com.example.studyplanner.dto.RegisterRequest;
import com.example.studyplanner.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthService.AuthResponse> register(
            @RequestBody RegisterRequest request
    ) {
        AuthService.AuthResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService.AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        AuthService.AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        authService.changePassword(
                userId,
                request
        );

        return ResponseEntity.noContent().build();
    }
}
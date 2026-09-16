package com.example.studyplanner.controller;

import com.example.studyplanner.entity.User;
import com.example.studyplanner.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        User user = userService.getUser(userId);

        return ResponseEntity.ok(
                new UserProfileResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                )
        );
    }

    @PutMapping("/me/username")
    public ResponseEntity<UserProfileResponse> updateUsername(
            @RequestBody UpdateUsernameRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        User user = userService.updateUsername(
                userId,
                request.username()
        );

        return ResponseEntity.ok(
                new UserProfileResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getUsername(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                )
        );
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody UpdatePasswordRequest request,
            Authentication authentication
    ) {
        UUID userId = (UUID) authentication.getPrincipal();

        userService.updatePassword(
                userId,
                request.currentPassword(),
                request.newPassword()
        );

        return ResponseEntity.noContent().build();
    }

    public record UpdateUsernameRequest(
            String username
    ) {
    }

    public record UpdatePasswordRequest(
            String currentPassword,
            String newPassword
    ) {
    }

    public record UserProfileResponse(
            UUID id,
            String email,
            String username,
            java.time.LocalDateTime createdAt,
            java.time.LocalDateTime updatedAt
    ) {
    }
}
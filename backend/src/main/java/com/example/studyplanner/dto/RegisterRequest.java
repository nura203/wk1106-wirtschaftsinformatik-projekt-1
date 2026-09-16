package com.example.studyplanner.dto;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
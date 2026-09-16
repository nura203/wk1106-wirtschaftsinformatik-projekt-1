package com.example.studyplanner.dto;

public record LoginRequest(
        String email,
        String password
) {
}
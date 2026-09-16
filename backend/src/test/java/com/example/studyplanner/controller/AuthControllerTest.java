package com.example.studyplanner.controller;

import com.example.studyplanner.dto.ChangePasswordRequest;
import com.example.studyplanner.dto.LoginRequest;
import com.example.studyplanner.dto.RegisterRequest;
import com.example.studyplanner.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .build();
    }

    @Test
    void registerReturnsCreated() throws Exception {
        UUID userId = UUID.randomUUID();

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(
                        new AuthService.AuthResponse(
                                "test-token",
                                userId,
                                "testuser"
                        )
                );

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "username": "testuser",
                                          "email": "test@example.com",
                                          "password": "TestPassword123!"
                                        }
                                        """)
                )
                .andExpect(status().isCreated());

        verify(authService)
                .register(any(RegisterRequest.class));
    }

    @Test
    void loginReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(
                        new AuthService.AuthResponse(
                                "test-token",
                                userId,
                                "testuser"
                        )
                );

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "test@example.com",
                                          "password": "TestPassword123!"
                                        }
                                        """)
                )
                .andExpect(status().isOk());

        verify(authService)
                .login(any(LoginRequest.class));
    }

    @Test
    void changePasswordReturnsNoContent() throws Exception {
        UUID userId = UUID.randomUUID();

        when(authentication.getPrincipal())
                .thenReturn(userId);

        mockMvc.perform(
                        put("/auth/password")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "password": "NewPassword123!"
                                        }
                                        """)
                )
                .andExpect(status().isNoContent());

        verify(authService)
                .changePassword(
                        any(UUID.class),
                        any(ChangePasswordRequest.class)
                );
    }
}
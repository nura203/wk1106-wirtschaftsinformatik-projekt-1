package com.example.studyplanner.service;

import com.example.studyplanner.dto.ChangePasswordRequest;
import com.example.studyplanner.dto.LoginRequest;
import com.example.studyplanner.dto.RegisterRequest;
import com.example.studyplanner.entity.User;
import com.example.studyplanner.repository.UserRepository;
import com.example.studyplanner.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserAndReturnsToken() {
        UUID userId = UUID.randomUUID();

        RegisterRequest request =
                new RegisterRequest(
                        "testuser",
                        "test@example.com",
                        "TestPassword123!"
                );

        User savedUser = new User();
        savedUser.setUsername("testuser");
        savedUser.setEmail("test@example.com");

        setUserId(savedUser, userId);

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        when(jwtService.generateToken(userId))
                .thenReturn("test-jwt-token");

        AuthService.AuthResponse response =
                authService.register(request);

        assertEquals("test-jwt-token", response.token());
        assertEquals(userId, response.userId());
        assertEquals("testuser", response.username());

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(userId);
    }

    @Test
    void registerRejectsEmptyUsername() {
        RegisterRequest request =
                new RegisterRequest(
                        "",
                        "test@example.com",
                        "TestPassword123!"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void registerRejectsEmptyEmail() {
        RegisterRequest request =
                new RegisterRequest(
                        "testuser",
                        "",
                        "TestPassword123!"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void registerRejectsShortPassword() {
        RegisterRequest request =
                new RegisterRequest(
                        "testuser",
                        "test@example.com",
                        "short"
                );

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request =
                new RegisterRequest(
                        "testuser",
                        "test@example.com",
                        "TestPassword123!"
                );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request)
        );
    }

    @Test
    void loginReturnsTokenForCorrectCredentials() {
        UUID userId = UUID.randomUUID();

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash(
                encoder.encode("TestPassword123!")
        );

        setUserId(user, userId);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(userId))
                .thenReturn("test-jwt-token");

        AuthService.AuthResponse response =
                authService.login(
                        new LoginRequest(
                                "test@example.com",
                                "TestPassword123!"
                        )
                );

        assertEquals("test-jwt-token", response.token());
        assertEquals(userId, response.userId());
        assertEquals("testuser", response.username());
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(
                        new LoginRequest(
                                "unknown@example.com",
                                "TestPassword123!"
                        )
                )
        );
    }

    @Test
    void loginRejectsWrongPassword() {
        UUID userId = UUID.randomUUID();

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash(
                encoder.encode("CorrectPassword123!")
        );

        setUserId(user, userId);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(
                        new LoginRequest(
                                "test@example.com",
                                "WrongPassword123!"
                        )
                )
        );
    }

    @Test
    void changePasswordUpdatesPasswordHash() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("old-hash");

        setUserId(user, userId);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        authService.changePassword(
                userId,
                new ChangePasswordRequest(
                        "NewPassword123!"
                )
        );

        assertNotNull(user.getPasswordHash());
        assertEquals(
                60,
                user.getPasswordHash().length()
        );

        verify(userRepository).save(user);
    }

    @Test
    void changePasswordRejectsShortPassword() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.changePassword(
                        userId,
                        new ChangePasswordRequest("short")
                )
        );
    }

    @Test
    void changePasswordRejectsUnknownUser() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.changePassword(
                        userId,
                        new ChangePasswordRequest(
                                "NewPassword123!"
                        )
                )
        );
    }

    private void setUserId(User user, UUID userId) {
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, userId);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Test konnte die User-ID nicht setzen",
                    exception
            );
        }
    }
}
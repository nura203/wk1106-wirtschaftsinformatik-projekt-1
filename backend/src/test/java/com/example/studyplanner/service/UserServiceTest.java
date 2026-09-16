package com.example.studyplanner.service;

import com.example.studyplanner.entity.User;
import com.example.studyplanner.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserReturnsUser() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User result = userService.getUser(userId);

        assertEquals(user, result);
    }

    @Test
    void getUserRejectsUnknownUser() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUser(userId)
        );
    }

    @Test
    void updateUsernameUpdatesUser() {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setUsername("alterName");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.save(user))
                .thenReturn(user);

        User result =
                userService.updateUsername(
                        userId,
                        "neuerName"
                );

        assertEquals("neuerName", result.getUsername());

        verify(userRepository).save(user);
    }

    @Test
    void updateUsernameRejectsEmptyUsername() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUsername(
                        userId,
                        ""
                )
        );
    }

    @Test
    void updateUsernameRejectsNullUsername() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUsername(
                        userId,
                        null
                )
        );
    }

    @Test
    void updatePasswordUpdatesPassword() {
        UUID userId = UUID.randomUUID();

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        User user = new User();

        user.setPasswordHash(
                encoder.encode("OldPassword123!")
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updatePassword(
                userId,
                "OldPassword123!",
                "NewPassword123!"
        );

        assertNotNull(user.getPasswordHash());

        assertEquals(
                60,
                user.getPasswordHash().length()
        );

        verify(userRepository).save(user);
    }

    @Test
    void updatePasswordRejectsEmptyCurrentPassword() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePassword(
                        userId,
                        "",
                        "NewPassword123!"
                )
        );
    }

    @Test
    void updatePasswordRejectsShortNewPassword() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePassword(
                        userId,
                        "OldPassword123!",
                        "short"
                )
        );
    }

    @Test
    void updatePasswordRejectsWrongCurrentPassword() {
        UUID userId = UUID.randomUUID();

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder(12);

        User user = new User();

        user.setPasswordHash(
                encoder.encode("CorrectPassword123!")
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePassword(
                        userId,
                        "WrongPassword123!",
                        "NewPassword123!"
                )
        );
    }

    @Test
    void updatePasswordRejectsNullCurrentPassword() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePassword(
                        userId,
                        null,
                        "NewPassword123!"
                )
        );
    }

    @Test
    void updatePasswordRejectsNullNewPassword() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.updatePassword(
                        userId,
                        "OldPassword123!",
                        null
                )
        );
    }
}
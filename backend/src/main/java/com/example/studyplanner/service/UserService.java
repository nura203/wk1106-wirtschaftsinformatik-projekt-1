package com.example.studyplanner.service;

import com.example.studyplanner.entity.User;
import com.example.studyplanner.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Transactional(readOnly = true)
    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Benutzer nicht gefunden"
                        )
                );
    }

    @Transactional
    public User updateUsername(
            UUID userId,
            String username
    ) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Benutzername darf nicht leer sein"
            );
        }

        User user = getUser(userId);

        user.setUsername(username);

        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(
            UUID userId,
            String currentPassword,
            String newPassword
    ) {
        if (currentPassword == null
                || currentPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Aktuelles Passwort darf nicht leer sein"
            );
        }

        if (newPassword == null
                || newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Neues Passwort muss mindestens 8 Zeichen lang sein"
            );
        }

        User user = getUser(userId);

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "Aktuelles Passwort ist falsch"
            );
        }

        user.setPasswordHash(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);
    }
}
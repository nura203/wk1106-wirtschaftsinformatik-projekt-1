package com.example.studyplanner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException exception
    ) {
        HttpStatus status =
                HttpStatus.valueOf(exception.getStatusCode().value());

        Map<String, Object> body = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", exception.getReason() != null
                        ? exception.getReason()
                        : status.getReasonPhrase(),
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            DateTimeException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(
            Exception exception
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> body = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", exception.getMessage() != null
                        ? exception.getMessage()
                        : "Ungültige Anfrage",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(
            Exception exception
    ) {
        HttpStatus status =
                HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> body = Map.of(
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", "Interner Serverfehler",
                "timestamp", LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(body);
    }
}
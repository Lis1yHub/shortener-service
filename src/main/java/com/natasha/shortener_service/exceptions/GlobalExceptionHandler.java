package com.natasha.shortener_service.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<String> linkNotFoundException(LinkNotFoundException ex) {

        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(LinkExpiredException.class)
    public ResponseEntity<String> linkExpiredException(LinkExpiredException ex) {

        return ResponseEntity.status(410).body(ex.getMessage());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> rateLimitExceededException(RateLimitExceededException ex) {

        return ResponseEntity.status(429).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> methodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        return ResponseEntity.status(400).body(ex
                .getFieldErrors()
                .get(0)
                .getDefaultMessage()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception(Exception ex) {

        return ResponseEntity.status(500).body("Internal server error");
    }

    @ExceptionHandler(ShortCodeGenerationException.class)
    public ResponseEntity<String> shortCodeGenerationException(ShortCodeGenerationException ex) {

        return ResponseEntity.status(500).body(ex.getMessage());
    }

}

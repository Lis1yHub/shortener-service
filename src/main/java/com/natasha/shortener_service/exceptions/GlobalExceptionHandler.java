package com.natasha.shortener_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<String> linkNotFoundException(LinkNotFoundException ex) {

        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(LinkExpiredException.class)
    public ResponseEntity<String> linkExpiredException(LinkExpiredException ex) {

        return ResponseEntity.status(401).body(ex.getMessage());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> rateLimitExceededException(RateLimitExceededException ex) {

        return ResponseEntity.status(429).body(ex.getMessage());
    }

}

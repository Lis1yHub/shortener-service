package com.natasha.shortener_service.exceptions;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Too many requests. Please try again later.");
    }
}

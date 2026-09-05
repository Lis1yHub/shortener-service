package com.natasha.shortener_service.exceptions;

public class ShortCodeGenerationException extends RuntimeException{
    public  ShortCodeGenerationException() {
        super("Failed to generate unique short code after 3 attempts");
    }
}

package com.natasha.shortener_service.exceptions;

public class AppException extends RuntimeException{
    public AppException(String message) {
        super(message);
    }
}

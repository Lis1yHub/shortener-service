package com.natasha.shortener_service.exceptions;

public class LinkNotFoundException extends AppException{
    public LinkNotFoundException(String shortCode) {
        super("Link " + shortCode + " not found");
    }
}

package com.natasha.shortener_service.exceptions;

public class LinkExpiredException extends AppException {
    public LinkExpiredException(String shortCode) {
        super("Link " + shortCode + " has expired");
    }
}

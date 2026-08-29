package com.natasha.shortener_service.exceptions;

public class EventSerializationException extends AppException {
    public EventSerializationException(Throwable cause) {
        super("Failed to serialize LinkClickedEvent", cause);
    }
}

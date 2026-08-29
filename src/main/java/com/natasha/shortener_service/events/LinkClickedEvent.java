package com.natasha.shortener_service.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record LinkClickedEvent(
        String shortCode,
        String originalUrl,
        LocalDateTime clickedAt,
        String userAgent,
        String correlationId,
        UUID eventId
) {
}

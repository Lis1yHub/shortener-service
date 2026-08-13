package com.natasha.analytics_service.events;

import java.time.LocalDateTime;

public record LinkClickedEvent(
       String shortCode,
       String originalUrl,
       LocalDateTime clickedAt,
       String userAgent,
       String correlationId
) {
}

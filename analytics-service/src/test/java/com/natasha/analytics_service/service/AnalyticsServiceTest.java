package com.natasha.analytics_service.service;

import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.repository.ClickEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ClickEventRepository eventRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void saveClickEvent_savesEventToRepository() {

        LocalDateTime clickedAt = LocalDateTime.now();
        UUID eventId = UUID.randomUUID();

        LinkClickedEvent event = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                clickedAt,
                "Mozilla/5.0",
                "correlation-123",
                eventId
        );

        analyticsService.saveClickEvent(event);

        verify(eventRepository).insertIfNotExists(
                "abc123",
                "https://google.com",
                clickedAt,
                "Mozilla/5.0",
                "correlation-123",
                eventId
        );
    }

    @Test
    void saveClickEvent_sameShortCodeWithDifferentEventIds_savesTwoClicks() {

        LocalDateTime firstClickedAt = LocalDateTime.now();
        LocalDateTime secondClickedAt = LocalDateTime.now();

        UUID firstEventId = UUID.randomUUID();
        UUID secondEventId = UUID.randomUUID();

        LinkClickedEvent firstEvent = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                firstClickedAt,
                "Mozilla/5.0",
                "correlation-1",
                firstEventId
        );

        LinkClickedEvent secondEvent = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                secondClickedAt,
                "Mozilla/5.0",
                "correlation-2",
                secondEventId
        );

        analyticsService.saveClickEvent(firstEvent);
        analyticsService.saveClickEvent(secondEvent);

        verify(eventRepository).insertIfNotExists(
                "abc123",
                "https://google.com",
                firstClickedAt,
                "Mozilla/5.0",
                "correlation-1",
                firstEventId
        );

        verify(eventRepository).insertIfNotExists(
                "abc123",
                "https://google.com",
                secondClickedAt,
                "Mozilla/5.0",
                "correlation-2",
                secondEventId
        );
    }

    @Test
    void getClicksCount_returnsCountFromRepository() {

        String shortCode = "abc123";

        when(eventRepository.countByShortCode(shortCode)).thenReturn(2);

        int result = analyticsService.getClicksCount(shortCode);

        assertEquals(2, result);

        verify(eventRepository).countByShortCode(shortCode);
    }
}
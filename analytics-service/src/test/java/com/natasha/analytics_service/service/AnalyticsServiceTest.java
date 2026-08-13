package com.natasha.analytics_service.service;

import com.natasha.analytics_service.entity.ClickEvent;
import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.repository.ClickEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

        LinkClickedEvent event = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                clickedAt,
                "Mozilla/5.0",
                "correlation-123"
        );

        analyticsService.saveClickEvent(event);

        ArgumentCaptor<ClickEvent> captor =
                ArgumentCaptor.forClass(ClickEvent.class);

        verify(eventRepository).save(captor.capture());

        ClickEvent savedEvent = captor.getValue();

        assertEquals("abc123", savedEvent.getShortCode());
        assertEquals("https://google.com", savedEvent.getOriginalUrl());
        assertEquals(clickedAt, savedEvent.getClickedAt());
        assertEquals("Mozilla/5.0", savedEvent.getUserAgent());
        assertEquals("correlation-123", savedEvent.getCorrelationId());
    }

    @Test
    void saveClickEvent_sameShortCodeTwice_savesTwoClicks() {

        LinkClickedEvent firstEvent = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                LocalDateTime.now(),
                "Mozilla/5.0",
                "correlation-1"
        );

        LinkClickedEvent secondEvent = new LinkClickedEvent(
                "abc123",
                "https://google.com",
                LocalDateTime.now(),
                "Mozilla/5.0",
                "correlation-2"
        );

        analyticsService.saveClickEvent(firstEvent);
        analyticsService.saveClickEvent(secondEvent);

        verify(eventRepository, times(2))
                .save(any(ClickEvent.class));
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

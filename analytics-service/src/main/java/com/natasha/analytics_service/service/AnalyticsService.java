package com.natasha.analytics_service.service;

import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ClickEventRepository eventRepository;

    @Transactional
    public void saveClickEvent(LinkClickedEvent linkClickedEvent) {

        eventRepository.insertIfNotExists(
            linkClickedEvent.shortCode(),
            linkClickedEvent.originalUrl(),
            linkClickedEvent.clickedAt(),
            linkClickedEvent.userAgent(),
            linkClickedEvent.correlationId(),
            linkClickedEvent.eventId()
        );
    }

    public int getClicksCount(String shortCode) {

        return eventRepository.countByShortCode(shortCode);
    }
}

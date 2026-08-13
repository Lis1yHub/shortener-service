package com.natasha.analytics_service.service;

import com.natasha.analytics_service.entity.ClickEvent;
import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ClickEventRepository eventRepository;

    public void saveClickEvent(LinkClickedEvent linkClickedEvent) {

        ClickEvent clickEvent = new ClickEvent();
        clickEvent.setShortCode(linkClickedEvent.shortCode());
        clickEvent.setOriginalUrl(linkClickedEvent.originalUrl());
        clickEvent.setClickedAt(linkClickedEvent.clickedAt());
        clickEvent.setUserAgent(linkClickedEvent.userAgent());
        clickEvent.setCorrelationId(linkClickedEvent.correlationId());

        eventRepository.save(clickEvent);
    }

    public int getClicksCount(String shortCode) {

        return eventRepository.countByShortCode(shortCode);
    }

}

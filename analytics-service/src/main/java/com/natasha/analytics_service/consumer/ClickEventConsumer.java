package com.natasha.analytics_service.consumer;

import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickEventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "link-clicks")
    public void consume(LinkClickedEvent linkClickedEvent) {

        analyticsService.saveClickEvent(linkClickedEvent);
    }
}

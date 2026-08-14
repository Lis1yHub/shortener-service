package com.natasha.analytics_service.consumer;

import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClickEventConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(topics = "link-clicks")
    public void consume(LinkClickedEvent linkClickedEvent) {

        try {
            MDC.put("correlationId", linkClickedEvent.correlationId());

            log.info(
                    "Received click event for shortCode={}",
                    linkClickedEvent.shortCode()
            );

            analyticsService.saveClickEvent(linkClickedEvent);
        } finally {
            MDC.clear();
        }
    }
}
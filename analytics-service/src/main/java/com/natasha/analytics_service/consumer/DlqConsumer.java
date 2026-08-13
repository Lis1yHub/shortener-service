package com.natasha.analytics_service.consumer;

import com.natasha.analytics_service.events.LinkClickedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DlqConsumer {

    @KafkaListener(topics = "link-clicks-dead-letter")
    public void consume(LinkClickedEvent linkClickedEvent) {

        log.error("Received event in DLQ: {}", linkClickedEvent);
    }

}

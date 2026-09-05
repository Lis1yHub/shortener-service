package com.natasha.analytics_service.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natasha.analytics_service.entity.FailedEvent;
import com.natasha.analytics_service.events.LinkClickedEvent;
import com.natasha.analytics_service.repository.FailedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class DlqConsumer {

    private final FailedEventRepository failedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "link-clicks-dead-letter")
    public void consume(ConsumerRecord<String, LinkClickedEvent> record) {

        LinkClickedEvent linkClickedEvent = record.value();

        Header errorHeader = record.headers()
                .lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);

        FailedEvent failedEvent = new FailedEvent();
        failedEvent.setEventId(linkClickedEvent.eventId());
        failedEvent.setCreatedAt(LocalDateTime.now());

        if (errorHeader != null) {
            String lastHeader = new String(
                    errorHeader.value(), StandardCharsets.UTF_8
            );
            failedEvent.setErrorMessage(lastHeader);

        } else {
            failedEvent.setErrorMessage("DLQ exception message is unavailable");
        }

        try {
            failedEvent.setPayload(objectMapper
                    .writeValueAsString(linkClickedEvent));

        } catch (JsonProcessingException ex) {
            log.error("Failed to serialize DLQ event, eventId={}",
                    linkClickedEvent.eventId(),
                    ex
            );
            throw new RuntimeException("Failed to serialize DLQ event", ex);
        }

        failedEventRepository.save(failedEvent);

        log.error("Received event in DLQ: {}", linkClickedEvent);
    }
}

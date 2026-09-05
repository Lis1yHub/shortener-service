package com.natasha.shortener_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natasha.shortener_service.events.LinkClickedEvent;
import com.natasha.shortener_service.models.OutboxEvent;
import com.natasha.shortener_service.models.OutboxStatus;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {

        List<OutboxEvent> listEvents = outboxEventRepository
                .findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for (OutboxEvent event : listEvents) {

            try {
                LinkClickedEvent clickedEvent = objectMapper
                        .readValue(
                                event.getPayload(),
                                LinkClickedEvent.class
                        );

                kafkaTemplate
                        .send("link-clicks",
                                event.getAggregateId(),
                                clickedEvent)
                        .get();

                event.setStatus(OutboxStatus.SENT);
                event.setSentAt(LocalDateTime.now());

                outboxEventRepository.save(event);

            } catch (JsonProcessingException ex) {

                event.setStatus(OutboxStatus.FAILED);
                outboxEventRepository.save(event);

                log.error("Failed to deserialize outbox event id={}", event.getId(), ex);

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                log.error("Outbox publishing was interrupted for event id={}",
                        event.getId(), ex);

                return;

            } catch (ExecutionException ex) {

                 log.error("Failed to publish outbox event to Kafka, event id={}", event.getId(), ex);
            }
        }
    }
}

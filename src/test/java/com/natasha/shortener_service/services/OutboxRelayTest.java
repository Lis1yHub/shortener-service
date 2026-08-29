package com.natasha.shortener_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natasha.shortener_service.events.LinkClickedEvent;
import com.natasha.shortener_service.models.OutboxEvent;
import com.natasha.shortener_service.models.OutboxStatus;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxRelayTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;

    private ObjectMapper objectMapper;

    private OutboxRelay outboxRelay;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        outboxRelay = new OutboxRelay(
                outboxEventRepository,
                kafkaTemplate,
                objectMapper
        );
    }

    @Test
    void publishPendingEvents_whenKafkaAvailable_marksEventAsSent() throws Exception {

        UUID eventId = UUID.randomUUID();

        LinkClickedEvent clickedEvent = new LinkClickedEvent(
                "abc12345",
                "https://google.com",
                LocalDateTime.now(),
                "Mozilla/5.0",
                "correlation-123",
                eventId
        );

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(eventId);
        outboxEvent.setAggregateType("Link");
        outboxEvent.setAggregateId("abc12345");
        outboxEvent.setEventType("click");
        outboxEvent.setPayload(objectMapper.writeValueAsString(clickedEvent));
        outboxEvent.setStatus(OutboxStatus.PENDING);

        when(outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING
        )).thenReturn(List.of(outboxEvent));

        CompletableFuture<SendResult<String, LinkClickedEvent>> future =
                CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(
                "link-clicks",
                "abc12345",
                clickedEvent
        )).thenReturn(future);

        outboxRelay.publishPendingEvents();

        assertEquals(OutboxStatus.SENT, outboxEvent.getStatus());
        assertNotNull(outboxEvent.getSentAt());

        verify(kafkaTemplate).send(
                "link-clicks",
                "abc12345",
                clickedEvent
        );

        verify(outboxEventRepository).save(outboxEvent);
    }

    @Test
    void publishPendingEvents_whenKafkaFails_keepsEventPending() throws Exception {

        UUID eventId = UUID.randomUUID();

        LinkClickedEvent clickedEvent = new LinkClickedEvent(
                "abc12345",
                "https://google.com",
                LocalDateTime.now(),
                "Mozilla/5.0",
                "correlation-123",
                eventId
        );

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(eventId);
        outboxEvent.setAggregateType("Link");
        outboxEvent.setAggregateId("abc12345");
        outboxEvent.setEventType("click");
        outboxEvent.setPayload(objectMapper.writeValueAsString(clickedEvent));
        outboxEvent.setStatus(OutboxStatus.PENDING);

        when(outboxEventRepository.findTop100ByStatusOrderByCreatedAtAsc(
                OutboxStatus.PENDING
        )).thenReturn(List.of(outboxEvent));

        CompletableFuture<SendResult<String, LinkClickedEvent>> failedFuture =
                CompletableFuture.failedFuture(
                        new RuntimeException("Kafka unavailable")
                );

        when(kafkaTemplate.send(
                "link-clicks",
                "abc12345",
                clickedEvent
        )).thenReturn(failedFuture);

        outboxRelay.publishPendingEvents();

        assertEquals(OutboxStatus.PENDING, outboxEvent.getStatus());
        assertNull(outboxEvent.getSentAt());

        verify(outboxEventRepository, never()).save(outboxEvent);
    }
}
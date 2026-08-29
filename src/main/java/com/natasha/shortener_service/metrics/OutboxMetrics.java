package com.natasha.shortener_service.metrics;

import com.natasha.shortener_service.models.OutboxStatus;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxMetrics {

    private final MeterRegistry meterRegistry;
    private final OutboxEventRepository outboxEventRepository;

    @PostConstruct
    public void init() {

        Gauge.builder("outbox.pending.count",
                () -> outboxEventRepository.countByStatus(OutboxStatus.PENDING)
        ).register(meterRegistry);
    }
}

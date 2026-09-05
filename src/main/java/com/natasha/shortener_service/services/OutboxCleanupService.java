package com.natasha.shortener_service.services;

import com.natasha.shortener_service.models.OutboxStatus;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxCleanupService {

    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOutboxEvent() {

        outboxEventRepository
                .deleteByStatusAndSentAtBefore(
                        OutboxStatus.SENT,
                        LocalDateTime.now().minusDays(7)
                );
    }
}

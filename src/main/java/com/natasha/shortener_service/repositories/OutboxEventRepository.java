package com.natasha.shortener_service.repositories;

import com.natasha.shortener_service.models.OutboxEvent;
import com.natasha.shortener_service.models.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

     List<OutboxEvent> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);

     long countByStatus(OutboxStatus status);
}

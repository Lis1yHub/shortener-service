package com.natasha.analytics_service.repository;

import com.natasha.analytics_service.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {

    int countByShortCode(String shortCode);

    @Modifying
    @Query(value = """
        INSERT INTO click_events
            (short_code, original_url, clicked_at, user_agent, correlation_id, event_id)
        VALUES
            (:shortCode, :originalUrl, :clickedAt, :userAgent, :correlationId, :eventId)
        ON CONFLICT (event_id) DO NOTHING
        """, nativeQuery = true)
    int insertIfNotExists(
            @Param("shortCode") String shortCode,
            @Param("originalUrl") String originalUrl,
            @Param("clickedAt") LocalDateTime clickedAt,
            @Param("userAgent") String userAgent,
            @Param("correlationId") String correlationId,
            @Param("eventId") UUID eventId
    );
}

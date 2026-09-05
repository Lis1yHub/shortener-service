package com.natasha.analytics_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "failed_events")
@Getter
@Setter
public class FailedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;

    @Column(name = "event_id", nullable = false)
    UUID eventId;

    @Column(name = "payload", nullable = false)
    String payload;

    @Column(name = "error_message", nullable = false)
    String errorMessage;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
}

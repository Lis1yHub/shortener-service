package com.natasha.shortener_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.events.LinkClickedEvent;
import com.natasha.shortener_service.exceptions.EventSerializationException;
import com.natasha.shortener_service.exceptions.LinkExpiredException;
import com.natasha.shortener_service.exceptions.LinkNotFoundException;
import com.natasha.shortener_service.exceptions.ShortCodeGenerationException;
import com.natasha.shortener_service.models.OutboxEvent;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.natasha.shortener_service.repositories.LinkRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final LinkLookupService linkLookupService;
    private final MeterRegistry meterRegistry;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final LinkPersistenceService linkPersistenceService;

    public Link createLink(CreateLinkRequest linkRequest) {

        for (int i = 0; i < 3; i++) {

            Link link = new Link();

            link.setShortCode(generateShortCode());
            link.setOriginalUrl(linkRequest.getOriginalUrl());
            link.setCreatedAt(LocalDateTime.now());
            link.setExpiresAt(linkRequest.getExpiresAt());

            try {
                Link savedLink = linkPersistenceService.save(link);
                meterRegistry.counter("links.creation").increment();

                return savedLink;

            } catch (DataIntegrityViolationException ex) {

                if (isShortCodeConflict(ex)) {
                     continue;
                } else {
                    throw ex;
                }
            }
        }

        throw new ShortCodeGenerationException();
    }

    @Transactional
    public Link getLinkForRedirect(String shortCode, String userAgent) {

        Link link = linkLookupService.getLinkByShortCode(shortCode);

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException(shortCode);
        }

        linkRepository.incrementClicks(shortCode);

        meterRegistry.counter("links.clicks").increment();

        String correlationId = MDC.get("correlationId");

        OutboxEvent outboxEvent = new OutboxEvent();

        LinkClickedEvent event = new LinkClickedEvent(
                link.getShortCode(),
                link.getOriginalUrl(),
                LocalDateTime.now(),
                userAgent,
                correlationId,
                outboxEvent.getId());

        outboxEvent.setAggregateType("Link");
        outboxEvent.setAggregateId(link.getShortCode());
        outboxEvent.setEventType("click");
        try {
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));

        } catch (JsonProcessingException ex) {
            throw new EventSerializationException(ex);
        }

        outboxEventRepository.save(outboxEvent);

        return link;
    }

    public Link getLinkInfo(String shortCode) {

        return linkLookupService.getLinkByShortCode(shortCode);
    }

    @Transactional
    @CacheEvict(value="links", key="#shortCode")
    public void deleteLink(String shortCode) {

        Link link = linkLookupService.getLinkByShortCode(shortCode);

        linkRepository.delete(link);
    }

    public int getLinkStats(String shortCode) {

        Optional<Link> optionalLink = linkRepository.findByShortCode(shortCode);
        Link link = optionalLink.orElseThrow(() -> new LinkNotFoundException(shortCode));

        return link.getClicks();
    }

    private String generateShortCode() {

        String shortCode = UUID.randomUUID().toString().substring(0, 8);

        return shortCode;
    }

    private boolean isShortCodeConflict(DataIntegrityViolationException ex) {

        Throwable cause = ex;
        while (cause != null) {

            if (cause.getMessage() != null &&
                    cause.getMessage()
                            .contains("uk_links_short_code")) {

                return true;

            } else {
                cause = cause.getCause();
            }
        }

        return false;
    }
}

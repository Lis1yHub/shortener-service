package com.natasha.shortener_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.events.LinkClickedEvent;
import com.natasha.shortener_service.exceptions.EventSerializationException;
import com.natasha.shortener_service.exceptions.LinkExpiredException;
import com.natasha.shortener_service.models.OutboxEvent;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import com.natasha.shortener_service.repositories.LinkRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final LinkLookupService linkLookupService;
    private final MeterRegistry meterRegistry;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public Link createLink(CreateLinkRequest linkRequest) {

        Link link = new Link();

        link.setShortCode(generateShortCode());
        link.setOriginalUrl(linkRequest.getOriginalUrl());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(linkRequest.getExpiresAt());

        Link savedLink = linkRepository.save(link);

        meterRegistry.counter("links.creation").increment();

        return savedLink;
    }

    @CachePut(value = "links", key = "#shortCode")
    @Transactional
    public Link getLinkForRedirect(String shortCode, String userAgent) {

        Link link = linkLookupService.getLinkByShortCode(shortCode);

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException(shortCode);
        }

        link.setClicks(link.getClicks() + 1);

        Link savedLink = linkRepository.save(link);

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

        return savedLink;
    }

    public Link getLinkInfo(String shortCode) {

        return linkLookupService.getLinkByShortCode(shortCode);
    }

    @CacheEvict(value="links", key="#shortCode")
    public void deleteLink(String shortCode) {

        Link link = linkLookupService.getLinkByShortCode(shortCode);

        linkRepository.delete(link);
    }

    public int getLinkStats(String shortCode) {

        Link link = linkLookupService.getLinkByShortCode(shortCode);

        return link.getClicks();
    }

    private String generateShortCode() {

        String shortCode;

        do {
            shortCode = UUID.randomUUID()
                    .toString()
                    .substring(0, 8);

        } while (linkRepository.existsByShortCode(shortCode));

        return shortCode;
    }

}

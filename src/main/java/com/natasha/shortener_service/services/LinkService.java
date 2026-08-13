package com.natasha.shortener_service.services;

import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.events.LinkClickedEvent;
import com.natasha.shortener_service.exceptions.LinkExpiredException;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.natasha.shortener_service.repositories.LinkRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;
    private final LinkLookupService linkLookupService;
    private final KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;

    public Link createLink(CreateLinkRequest linkRequest) {

        Link link = new Link();

        link.setShortCode(generateShortCode());
        link.setOriginalUrl(linkRequest.getOriginalUrl());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(linkRequest.getExpiresAt());

        return linkRepository.save(link);
    }

    @CachePut(value = "links", key = "#shortCode")
    public Link getLinkForRedirect(String shortCode, String userAgent, String correlationId) {

        Link link = linkLookupService.getLinkByShortCode(shortCode);

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException(shortCode);
        }

        link.setClicks(link.getClicks() + 1);

        Link savedLink = linkRepository.save(link);

        LinkClickedEvent event = new LinkClickedEvent(
                link.getShortCode(),
                link.getOriginalUrl(),
                LocalDateTime.now(),
                userAgent,
                correlationId);

        kafkaTemplate.send("link-clicks", shortCode, event);

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

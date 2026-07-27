package com.natasha.shortener_service.services;

import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.exceptions.LinkExpiredException;
import com.natasha.shortener_service.exceptions.LinkNotFoundException;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.springframework.stereotype.Service;
import com.natasha.shortener_service.repositories.LinkRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkRepository linkRepository;

    public Link createLink(CreateLinkRequest linkRequest) {

        Link link = new Link();

        link.setShortCode(generateShortCode());
        link.setOriginalUrl(linkRequest.getOriginalUrl());
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(linkRequest.getExpiresAt());

        return linkRepository.save(link);
    }

    public Link getLinkForRedirect(String shortCode) {

        Link link = getLinkByShortCode(shortCode);

        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new LinkExpiredException(shortCode);
        }

        link.setClicks(link.getClicks() + 1);

        return linkRepository.save(link);
    }

    public Link getLinkInfo(String shortCode) {

        return getLinkByShortCode(shortCode);
    }

    public void deleteLink(String shortCode) {

        Link link = getLinkByShortCode(shortCode);

        linkRepository.delete(link);
    }

    public int getLinkStats(String shortCode) {

        Link link = getLinkByShortCode(shortCode);

        return link.getClicks();
    }

    private Link getLinkByShortCode(String shortCode) {

        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));
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

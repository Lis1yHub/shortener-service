package com.natasha.shortener_service.services;

import com.natasha.shortener_service.exceptions.LinkNotFoundException;
import com.natasha.shortener_service.models.Link;
import com.natasha.shortener_service.repositories.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkLookupService {

    private final LinkRepository linkRepository;

    @Cacheable(value = "links", key = "#shortCode")
    public Link getLinkByShortCode(String shortCode) {

        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));
    }
}

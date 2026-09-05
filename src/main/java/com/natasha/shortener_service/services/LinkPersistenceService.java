package com.natasha.shortener_service.services;

import com.natasha.shortener_service.models.Link;
import com.natasha.shortener_service.repositories.LinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkPersistenceService {

    private final LinkRepository linkRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Link save(Link link) {

        return linkRepository.saveAndFlush(link);
    }
}

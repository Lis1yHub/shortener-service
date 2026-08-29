package com.natasha.shortener_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.exceptions.LinkNotFoundException;
import com.natasha.shortener_service.models.Link;
import com.natasha.shortener_service.repositories.LinkRepository;
import com.natasha.shortener_service.repositories.OutboxEventRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private LinkLookupService linkLookupService;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter counter;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LinkService linkService;

    private Link link;

    @BeforeEach
    void setUp() {

        link = new Link();

        link.setId(1L);
        link.setShortCode("abc12345");
        link.setOriginalUrl("https://google.com");
        link.setClicks(0);
    }

    @Test
    void createLink_success() {

        CreateLinkRequest request = new CreateLinkRequest();
        request.setOriginalUrl("https://google.com");

        when(linkRepository.existsByShortCode(any()))
                .thenReturn(false);

        when(linkRepository.save(any(Link.class)))
                .thenReturn(link);

        when(meterRegistry.counter("links.creation"))
                .thenReturn(counter);

        Link result = linkService.createLink(request);

        assertNotNull(result);
        assertEquals("https://google.com", result.getOriginalUrl());
        assertNotNull(result.getShortCode());

        verify(linkRepository).save(any(Link.class));
        verify(counter).increment();
    }

    @Test
    void getLinkInfo_existingShortCode_success() {

        when(linkLookupService.getLinkByShortCode("abc12345"))
                .thenReturn(link);

        Link result = linkService.getLinkInfo("abc12345");

        assertNotNull(result);
        assertEquals("abc12345", result.getShortCode());

        verify(linkLookupService)
                .getLinkByShortCode("abc12345");
    }

    @Test
    void getLinkInfo_notFound_throwException() {

        when(linkLookupService.getLinkByShortCode("wrong"))
                .thenThrow(new LinkNotFoundException("wrong"));

        assertThrows(
                LinkNotFoundException.class,
                () -> linkService.getLinkInfo("wrong")
        );

        verify(linkLookupService)
                .getLinkByShortCode("wrong");
    }

    @Test
    void getLinkForRedirect_whenLinkSaveFails_outboxIsNotSaved() {

        when(linkLookupService.getLinkByShortCode("abc12345"))
                .thenReturn(link);

        when(linkRepository.save(link))
                .thenThrow(new RuntimeException("Database error"));

        assertThrows(
                RuntimeException.class,
                () -> linkService.getLinkForRedirect(
                        "abc12345",
                        "Mozilla/5.0"
                )
        );

        verify(outboxEventRepository, never())
                .save(any());
    }
}
package com.natasha.shortener_service.services;

import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.exceptions.LinkNotFoundException;
import com.natasha.shortener_service.models.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.natasha.shortener_service.repositories.LinkRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

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

        when(linkRepository.existsByShortCode(any())).thenReturn(false);
        when(linkRepository.save(any(Link.class))).thenReturn(link);

        Link result = linkService.createLink(request);

        assertNotNull(result);
        assertEquals("https://google.com", result.getOriginalUrl());
        assertNotNull(result.getShortCode());

        verify(linkRepository).save(any(Link.class));
    }

    @Test
    void getLinkInfo_existingShortCode_success() {

        when(linkRepository.findByShortCode("abc12345")).thenReturn(Optional.of(link));

        Link result = linkService.getLinkInfo("abc12345");

        assertNotNull(result);
        assertEquals("abc12345", result.getShortCode());

        verify(linkRepository).findByShortCode("abc12345");
    }

    @Test
    void getLinkInfo_notFound_throwException() {

        when(linkRepository.findByShortCode("wrong")).thenReturn(Optional.empty());

        assertThrows(LinkNotFoundException.class, () -> linkService.getLinkInfo("wrong"));

        verify(linkRepository).findByShortCode("wrong");
    }
}

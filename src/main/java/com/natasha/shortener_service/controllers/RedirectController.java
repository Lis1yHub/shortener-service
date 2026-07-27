package com.natasha.shortener_service.controllers;

import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.natasha.shortener_service.services.LinkService;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        Link link = linkService.getLinkForRedirect(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", link.getOriginalUrl()).build();
    }
}

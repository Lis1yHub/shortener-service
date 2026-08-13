package com.natasha.shortener_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.natasha.shortener_service.services.LinkService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        String correlationId = request.getHeader("X-Correlation-Id");

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        Link link = linkService.getLinkForRedirect(shortCode, userAgent, correlationId);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", link.getOriginalUrl()).build();
    }
}

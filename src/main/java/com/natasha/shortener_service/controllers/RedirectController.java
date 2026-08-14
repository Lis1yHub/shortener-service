package com.natasha.shortener_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.natasha.shortener_service.services.LinkService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final LinkService linkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        log.info("Redirect request received for shortCode={}", shortCode);

        String userAgent = request.getHeader("User-Agent");

        Link link = linkService.getLinkForRedirect(shortCode, userAgent);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", link.getOriginalUrl()).build();
    }
}

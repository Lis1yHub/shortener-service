package com.natasha.shortener_service.controllers;

import com.natasha.shortener_service.dto.CreateLinkRequest;
import com.natasha.shortener_service.dto.LinkResponse;
import com.natasha.shortener_service.dto.LinkStatsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.natasha.shortener_service.models.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.natasha.shortener_service.services.LinkService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<LinkResponse> createLink(@Valid @RequestBody CreateLinkRequest request) {

        Link link = linkService.createLink(request);

        LinkResponse response = new LinkResponse();
        response.setShortUrl("http://localhost:8080/" + link.getShortCode());
        response.setShortCode(link.getShortCode());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{shortCode}/info")
    public ResponseEntity<LinkResponse> getInfo(@PathVariable String shortCode) {

        Link link = linkService.getLinkInfo(shortCode);

        LinkResponse response = new LinkResponse();
        response.setShortCode(link.getShortCode());
        response.setShortUrl("http://localhost:8080/" + link.getShortCode());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteLink(@PathVariable String shortCode) {

        linkService.deleteLink(shortCode);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<LinkStatsResponse> getLinkStats(@PathVariable String shortCode) {

        int clicks = linkService.getLinkStats(shortCode);

        LinkStatsResponse response = new LinkStatsResponse();
        response.setClicks(clicks);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}

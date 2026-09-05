package com.natasha.analytics_service.controller;

import com.natasha.analytics_service.dto.ClicksResponse;
import com.natasha.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<ClicksResponse> getAnalytics(@PathVariable String shortCode) {

        ClicksResponse clicksResponse = new
                ClicksResponse(analyticsService.getClicksCount(shortCode));

        return ResponseEntity.ok(clicksResponse);
    }
}

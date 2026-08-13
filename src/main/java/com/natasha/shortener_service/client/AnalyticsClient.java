package com.natasha.shortener_service.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Component
@RequiredArgsConstructor
public class AnalyticsClient {

    private final WebClient webClient;

    public Integer getClicksCount(String shortCode) {

        try {
            return webClient.get()
                    .uri("/api/analytics/{shortCode}", shortCode)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();

        } catch (WebClientException exception) {
            return 0;
        }

    }
}

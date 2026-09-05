package com.natasha.shortener_service.client;

import com.natasha.shortener_service.dto.ClicksResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class AnalyticsClient {

    private final WebClient webClient;

    public Integer getClicksCount(String shortCode) {

        try {
            return webClient.get()
                    .uri("/api/analytics/{shortCode}", shortCode)
                    .retrieve()
                    .bodyToMono(ClicksResponse.class)
                    .map(clicksResponse -> clicksResponse.clicks())
                    .timeout(Duration.ofSeconds(3))
                    .onErrorReturn(TimeoutException.class, 0)
                    .block();

        } catch (WebClientException exception) {
            return 0;
        }
    }
}

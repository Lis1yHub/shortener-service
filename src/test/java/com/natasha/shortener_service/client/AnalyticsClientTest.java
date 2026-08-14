package com.natasha.shortener_service.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticsClientTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    @InjectMocks
    private AnalyticsClient analyticsClient;

    @Test
    void getClicksCount_whenServiceUnavailable_returnsZero() {

        WebClientRequestException exception =
                new WebClientRequestException(
                        new ConnectException("Connection refused"),
                        HttpMethod.GET,
                        URI.create("http://localhost:8081/api/analytics/abc123"),
                        HttpHeaders.EMPTY
                );

        when(webClient.get()).thenThrow(exception);

        Integer result = analyticsClient.getClicksCount("abc123");

        assertEquals(0, result);
    }

    @Test
    void getClicksCount_whenRequestTimesOut_returnsZero() {

        when(webClient.get()
                .uri("/api/analytics/{shortCode}", "abc123")
                .retrieve()
                .bodyToMono(Integer.class))
                .thenReturn(Mono.error(new TimeoutException("Test timeout")));

        Integer result = analyticsClient.getClicksCount("abc123");

        assertEquals(0, result);
    }
}

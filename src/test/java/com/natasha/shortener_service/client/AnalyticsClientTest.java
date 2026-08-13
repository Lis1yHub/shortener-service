package com.natasha.shortener_service.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AnalyticsClientTest {

    @Mock
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
}

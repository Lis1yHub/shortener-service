package com.natasha.shortener_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value ("${analytics.base-url}") String analyticsBaseUrl) {
        return WebClient.builder().baseUrl(analyticsBaseUrl).build();
    }
}

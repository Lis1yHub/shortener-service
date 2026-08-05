package com.natasha.shortener_service.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitServiceTest {


    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RateLimitService rateLimitService;


    @BeforeEach
    void setUp() {

        rateLimitService = new RateLimitService(redisTemplate);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void firstRequest_shouldNotBeBlocked() {

        when(valueOperations.increment("rate_limit:127.0.0.1")).thenReturn(1L);

        boolean result = rateLimitService.isRateLimited("127.0.0.1");

        assertFalse(result);

        verify(redisTemplate).expire("rate_limit:127.0.0.1", Duration.ofMinutes(1));
    }

    @Test
    void eleventhRequest_shouldBeBlocked() {

        when(valueOperations.increment("rate_limit:127.0.0.1")).thenReturn(11L);

        boolean result = rateLimitService.isRateLimited("127.0.0.1");

        assertTrue(result);

        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }

    @Test
    void firstRequest_shouldSetTTL() {

        when(valueOperations.increment("rate_limit:127.0.0.1")).thenReturn(1L);

        rateLimitService.isRateLimited("127.0.0.1");

        verify(redisTemplate).expire("rate_limit:127.0.0.1", Duration.ofMinutes(1));
    }

    @Test
    void nextRequests_shouldNotResetTTL() {

        when(valueOperations.increment("rate_limit:127.0.0.1")).thenReturn(2L);

        rateLimitService.isRateLimited("127.0.0.1");

        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }
}
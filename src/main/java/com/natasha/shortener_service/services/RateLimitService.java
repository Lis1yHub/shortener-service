package com.natasha.shortener_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isRateLimited(String ip) {

        String key = "rate_limit:" + ip;

        try {
            Long requests = redisTemplate.opsForValue().increment(key);

            if (requests == null) {
                return false;
            }

            if (requests == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(1));
            }

            return requests > 10;

        } catch (RedisConnectionFailureException ex) {
            log.warn("Redis is unavailable, rate limiting is temporarily disabled", ex);

            return false;
        }
    }
}

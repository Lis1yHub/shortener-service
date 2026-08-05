package com.natasha.shortener_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isRateLimited(String ip) {

        String key = "rate_limit:" + ip;

        Long requests = redisTemplate.opsForValue().increment(key);

        if (requests == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        return requests > 10;
    }
}

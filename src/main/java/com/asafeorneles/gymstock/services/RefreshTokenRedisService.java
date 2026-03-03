package com.asafeorneles.gymstock.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenRedisService {

    final private StringRedisTemplate redisTemplate;

    private String buildKey(String jti){
        return "refresh:" + jti;
    }

    public void save(String jti, String userId, long expirationSeconds){
        String key = buildKey(jti);
        redisTemplate.opsForValue().set(key, userId, expirationSeconds, TimeUnit.SECONDS);
    }

    public boolean existsInRedis(String jti){
        String key = buildKey(jti);
        boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public void delete(String jti){
        String key = buildKey(jti);
        redisTemplate.delete(key);
    }
}

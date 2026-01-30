package com.example.smssender.Service;

import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class BlockListService {

    private static final String BLOCKED_NUMBERS_KEY = "sms:blocked:numbers";

    private final RedisTemplate<String, String> redisTemplate;

    public BlockListService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blockNumber(String phoneNumber) {
        redisTemplate.opsForSet().add(BLOCKED_NUMBERS_KEY, phoneNumber);
    }

    public void unblockNumber(String phoneNumber) {
        redisTemplate.opsForSet().remove(BLOCKED_NUMBERS_KEY, phoneNumber);
    }

    public boolean isBlocked(String phoneNumber) {
        Boolean isMember = redisTemplate.opsForSet().isMember(BLOCKED_NUMBERS_KEY, phoneNumber);
        return Boolean.TRUE.equals(isMember);
    }

    public Set<String> getAllBlockedNumbers() {
        return redisTemplate.opsForSet().members(BLOCKED_NUMBERS_KEY);
    }
}

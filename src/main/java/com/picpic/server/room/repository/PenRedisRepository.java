package com.picpic.server.room.repository;

import com.picpic.server.room.dto.PenRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.RedisTemplate;

@Repository
@RequiredArgsConstructor
public class PenRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveStroke(Long sessionId, Long memberId, PenRedisDTO dto) {
        String key = generateKey(sessionId, memberId);
        redisTemplate.opsForValue().set(key, dto);
    }

    private String generateKey(Long sessionId, Long memberId) {
        return "decorate:pen:" + sessionId + ":" + memberId;
    }
}

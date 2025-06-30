package com.picpic.server.room.repository;


import com.picpic.server.room.dto.TextRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TextRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveText(Long sessionId, String text, String font, String color, Long memberId, List<? extends Object> points) {
        String key = generateKey(sessionId, memberId);

        // points DTO를 Redis용 DTO로 매핑
        List<TextRedisDTO.Point> redisPoints = points.stream()
                .map(p -> {
                    if (p instanceof com.picpic.server.room.dto.DecorateTextRequestDTO.Point dtoPoint) {
                        return new TextRedisDTO.Point(dtoPoint.x(), dtoPoint.y());
                    } else {
                        throw new IllegalArgumentException("Unknown point type");
                    }
                })
                .toList();

        TextRedisDTO value = new TextRedisDTO(text, font, color, redisPoints);

        // 리스트로 저장 → 중복 저장 허용됨
        redisTemplate.opsForList().rightPush(key, value);
    }

    private String generateKey(Long sessionId, Long memberId) {
        return "decorate:text:" + sessionId + ":" + memberId;
    }
}

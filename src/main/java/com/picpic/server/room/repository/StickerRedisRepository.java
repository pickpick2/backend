package com.picpic.server.room.repository;

import com.picpic.server.room.dto.DecorateStickerRequestDTO;
import com.picpic.server.room.dto.StickerRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StickerRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveSticker(Long sessionId, Long stickerId, Long memberId, List<DecorateStickerRequestDTO.Point> points) {
        String key = generateKey(sessionId, stickerId);

        StickerRedisDTO value = new StickerRedisDTO(memberId, points);

        // Redis 리스트에 push (여러 사용자 스티커 저장 가능)
        redisTemplate.opsForList().rightPush(key, value);
    }

    public List<StickerRedisDTO> getStickerList(Long sessionId, Long stickerId) {
        String key = generateKey(sessionId, stickerId);
        List<Object> raw = redisTemplate.opsForList().range(key, 0, -1);

        // 타입 캐스팅
        return raw.stream()
                .filter(o -> o instanceof StickerRedisDTO)
                .map(o -> (StickerRedisDTO) o)
                .toList();
    }

    private String generateKey(Long sessionId, Long stickerId) {
        return "sticker:" + sessionId + ":" + stickerId;
    }
}

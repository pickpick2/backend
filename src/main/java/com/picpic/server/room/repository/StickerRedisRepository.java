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

    // 스티커 저장 (고유 stickerInstanceId 생성)
    public Long saveSticker(Long sessionId, Long stickerId, Long memberId, List<DecorateStickerRequestDTO.Point> points) {
        String key = generateKey(sessionId);
        Long stickerInstanceId = redisTemplate.opsForValue().increment("sticker:instance:id");

        StickerRedisDTO dto = new StickerRedisDTO(stickerInstanceId, stickerId, memberId, points);
        redisTemplate.opsForList().rightPush(key, dto);
        return stickerInstanceId;
    }

    // 스티커 위치 수정
    public void updateStickerPosition(Long sessionId, Long stickerInstanceId, List<DecorateStickerRequestDTO.Point> newPoints) {
        String key = generateKey(sessionId);
        List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);
        if (rawList == null) return;

        for (int i = 0; i < rawList.size(); i++) {
            Object item = rawList.get(i);
            if (item instanceof StickerRedisDTO dto && dto.stickerInstanceId().equals(stickerInstanceId)) {
                StickerRedisDTO updated = new StickerRedisDTO(
                        dto.stickerInstanceId(),
                        dto.stickerId(),
                        dto.memberId(),
                        newPoints
                );
                redisTemplate.opsForList().set(key, i, updated);
                break;
            }
        }
    }

    // 스티커 삭제
    public void deleteSticker(Long sessionId, Long stickerInstanceId) {
        String key = generateKey(sessionId);
        List<Object> rawList = redisTemplate.opsForList().range(key, 0, -1);
        if (rawList == null) return;

        for (Object item : rawList) {
            if (item instanceof StickerRedisDTO dto && dto.stickerInstanceId().equals(stickerInstanceId)) {
                redisTemplate.opsForList().remove(key, 1, dto);
                break;
            }
        }
    }

    // Redis 키 생성
    private String generateKey(Long sessionId) {
        return "sticker:" + sessionId;
    }
}

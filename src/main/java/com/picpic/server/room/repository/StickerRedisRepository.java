package com.picpic.server.room.repository;

import com.picpic.server.common.exception.ApiException;
import com.picpic.server.common.exception.ErrorCode;
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
    public Long saveSticker(Long roomId, Long stickerId, Long memberId,
                            Integer x, Integer y, Integer width, Integer height, Integer scale) {
        String key = generateKey(roomId);
        Long stickerInstanceId = redisTemplate.opsForValue().increment("sticker:instance:id");

        // Redis에 저장할 DTO 구성
        StickerRedisDTO dto = new StickerRedisDTO(
                stickerInstanceId,
                stickerId,
                memberId,
                x, y, width, height, scale
        );

        redisTemplate.opsForList().rightPush(key, dto);
        return stickerInstanceId;
    }

    // 스티커 위치 수정
    public StickerRedisDTO updateStickerPosition(Long roomId, Long stickerInstanceId, Integer x, Integer y) {
        String key = generateKey(roomId);
        List<Object> stickers = redisTemplate.opsForList().range(key, 0, -1);

        for (int i = 0; i < stickers.size(); i++) {
            StickerRedisDTO dto = (StickerRedisDTO) stickers.get(i);
            if (dto.stickerInstanceId().equals(stickerInstanceId)) {
                StickerRedisDTO updated = new StickerRedisDTO(
                        dto.stickerInstanceId(),
                        dto.stickerId(),
                        dto.memberId(),
                        x,
                        y,
                        dto.width(),
                        dto.height(),
                        dto.scale()
                );

                redisTemplate.opsForList().set(key, i, updated);
                return updated;
            }
        }

        throw new ApiException(ErrorCode.NO_STICKER); // 못 찾은 경우
    }

    // 스티커 삭제
    public void deleteSticker(Long roomId, Long stickerInstanceId) {
        String key = generateKey(roomId);
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
    private String generateKey(Long roomId) {
        return "sticker:" + roomId;
    }
}

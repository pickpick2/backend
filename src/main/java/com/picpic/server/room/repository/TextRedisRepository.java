package com.picpic.server.room.repository;



import com.picpic.server.room.dto.TextRedisDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TextRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveText(Long roomId, String textBoxId, String text, String font, Integer fontSize, String color, Integer x, Integer y, Long memberId) {
        String key = generateKey(roomId, textBoxId);

        TextRedisDTO value = new TextRedisDTO(
                textBoxId, text, font, fontSize, color, x, y,0,1.0
        );

        redisTemplate.opsForValue().set(key, value);
    }

//  텍스트 조회

    public Optional<TextRedisDTO> findText(Long roomId, String textBoxId) {
        String key = generateKey(roomId, textBoxId);
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof TextRedisDTO dto) {
            return Optional.of(dto);
        }
        return Optional.empty();
    }


//     * 텍스트 수정 (동일 키 덮어쓰기)

    public void updateText(Long roomId, TextRedisDTO dto) {
        String key = generateKey(roomId, dto.textBoxId());
        redisTemplate.opsForValue().set(key, dto);
    }


     //* 텍스트 위치만 수정

    public void updateTextPosition(Long roomId, String textBoxId, Integer newX, Integer newY) {
        String key = generateKey(roomId, textBoxId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof TextRedisDTO oldDto) {
            TextRedisDTO updated = new TextRedisDTO(
                    oldDto.textBoxId(),
                    oldDto.text(),
                    oldDto.font(),
                    oldDto.fontSize(),
                    oldDto.color(),
                    newX,
                    newY,
                    oldDto.rotate(),
                    oldDto.scale()
            );
            redisTemplate.opsForValue().set(key, updated);
        }
    }

// 텍스트 사이즈, 각도 수정
    public void updateTextTransform(Long roomId, String textBoxId, Double scale, Integer rotate) {
        String key = generateKey(roomId, textBoxId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof TextRedisDTO oldDto) {
            TextRedisDTO updated = new TextRedisDTO(
                    oldDto.textBoxId(),
                    oldDto.text(),
                    oldDto.font(),
                    oldDto.fontSize(),
                    oldDto.color(),
                    oldDto.x(),
                    oldDto.y(),
                    rotate,
                    scale
            );
            redisTemplate.opsForValue().set(key, updated);
        }
    }


     //* 텍스트 삭제

    public void deleteText(Long roomId, String textBoxId) {
        redisTemplate.delete(generateKey(roomId, textBoxId));
    }


     //* Redis 키 생성

    private String generateKey(Long roomId, String textBoxId) {
        return "decorate:text:" + roomId + ":" + textBoxId;
    }
}

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

    public void saveText(Long sessionId, String textBoxId, String text, String font, String color, Long memberId, List<? extends Object> points) {
        String key = generateKey(sessionId, textBoxId);

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

        TextRedisDTO value = new TextRedisDTO(textBoxId, text, font, color, redisPoints);

        redisTemplate.opsForValue().set(key, value);
    }
    /*
     * 텍스트 조회
     */
    public Optional<TextRedisDTO> findText(Long sessionId, String textBoxId) {
        String key = generateKey(sessionId, textBoxId);
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof TextRedisDTO dto) {
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    /*
     * 텍스트 수정 (동일 키 덮어쓰기)
     */
    public void updateText(Long sessionId, TextRedisDTO dto) {
        String key = generateKey(sessionId, dto.textBoxId());
        redisTemplate.opsForValue().set(key, dto);
    }

    /*
     * 텍스트 위치만 수정 (points만 업데이트)
     */
    public void updateTextPosition(Long sessionId, String textBoxId, List<TextRedisDTO.Point> newPoints) {
        String key = generateKey(sessionId, textBoxId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value instanceof TextRedisDTO oldDto) {
            TextRedisDTO updated = new TextRedisDTO(
                    oldDto.textBoxId(),
                    oldDto.text(),
                    oldDto.font(),
                    oldDto.color(),
                    newPoints
            );
            redisTemplate.opsForValue().set(key, updated);
        }
    }

//    /*
//     * 텍스트 삭제
//     */
//    public void deleteText(Long sessionId, String textBoxId) {
//        redisTemplate.delete(generateKey(sessionId, textBoxId));
//    }

    /*
     * Redis 키 생성
     */
    private String generateKey(Long sessionId, String textBoxId) {
        return "decorate:text:" + sessionId + ":" + textBoxId;
    }
}

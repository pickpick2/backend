package com.picpic.server.room.service;

import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetRoomMemberService implements GetRoomMemberUseCase {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Set<Object> getRoomMember(Long memberId, String roomId) {
        return redisTemplate.opsForSet().members("room:" + roomId + ":userNames");
    }
}

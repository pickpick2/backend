package com.picpic.server.room.service;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.service.redis.RedisRoomQuery;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetRoomMemberService implements GetRoomMemberUseCase {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisRoomQueryUseCase redisRomQuery;
    private final RedisRoomQuery redisRoomQuery;

    @Override
    public List<RoomMember> getRoomMember(Long memberId, String roomId) {
        return redisRoomQuery.searchMember(roomId);
    }
}

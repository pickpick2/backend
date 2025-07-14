package com.picpic.server.room.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.service.redis.RedisRoomQuery;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetRoomMemberService implements GetRoomMemberUseCase {

    private final RedisRoomQuery redisRoomQuery;

    @Override
    public List<RoomMember> getRoomMember(Long memberId, String roomId) {
        return redisRoomQuery.searchMember(roomId);
    }
}

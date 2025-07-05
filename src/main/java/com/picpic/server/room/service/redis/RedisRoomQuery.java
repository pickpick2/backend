package com.picpic.server.room.service.redis;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.repository.RoomRedisRepository;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisRoomQuery implements RedisRoomQueryUseCase {

    private final RoomRedisRepository roomRedisRepository;

    @Override
    public List<RoomMember> searchMember(String roomId) {
        return roomRedisRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found. :" + roomId))
                .getMembers();
    }

    @Override
    public boolean exist(String roomId) {
        return roomRedisRepository.existsById(roomId);
    }
}

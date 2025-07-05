package com.picpic.server.room.service;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.common.util.IdUtils;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.entity.RoomRedisEntity;
import com.picpic.server.room.entity.RoomHistoryEntity;
import com.picpic.server.room.repository.RoomHistoryRepository;
import com.picpic.server.room.repository.RoomRedisRepository;
import com.picpic.server.room.service.redis.RedisRoomQuery;
import com.picpic.server.room.service.usecase.CreateRoomUseCase;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import com.picpic.server.room.service.usecase.RoomHistoryCommandUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRoomService implements CreateRoomUseCase {

    private final RedisRoomCommandUseCase redisRoomCommandUseCase;
    private final RoomHistoryCommandUseCase roomHistoryCommandUseCase;

    @Override
    public String createRoom(MemberPrincipalDetail creatorPrinciple) {
        String roomId = UUID.randomUUID().toString();

        roomHistoryCommandUseCase.create(IdUtils.generateTsid(), creatorPrinciple.memberId());
        redisRoomCommandUseCase.create(roomId, creatorPrinciple);

        return roomId;
    }
}

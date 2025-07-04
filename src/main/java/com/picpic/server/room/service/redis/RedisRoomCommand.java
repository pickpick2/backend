package com.picpic.server.room.service.redis;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.entity.RoomRedisEntity;
import com.picpic.server.room.repository.RoomRedisRepository;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRoomCommand implements RedisRoomCommandUseCase {

    private final RoomRedisRepository roomRedisRepository;

    @Override
    public void create(String roomId, MemberPrincipalDetail memberPrincipal) {

        RoomRedisEntity room = RoomRedisEntity.builder()
                .roomId(roomId)
                .creator(RoomMember.from(memberPrincipal))
                .build();

        roomRedisRepository.save(room);
    }

    @Override
    public void delete(String roomId) {
        roomRedisRepository.deleteById(roomId);
    }

    @Override
    public void addMember(String roomId, MemberPrincipalDetail memberPrincipalDetail) {
        RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found. :" + roomId));

        RoomMember newMember = RoomMember.from(memberPrincipalDetail);
        RoomRedisEntity updatedEntity = roomEntity.addMember(newMember);

        roomRedisRepository.save(updatedEntity);
    }

    @Override
    public void subtractMember(String roomId, Long memberId) {
        RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found. :" + roomId));

        RoomRedisEntity updatedEntity = roomEntity.subtractMember(memberId);

        roomRedisRepository.save(updatedEntity);
    }
}

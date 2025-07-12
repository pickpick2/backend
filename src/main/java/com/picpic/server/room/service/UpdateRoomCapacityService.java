package com.picpic.server.room.service;

import org.springframework.stereotype.Service;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.ws.RoomCapacityResponseDto;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;
import com.picpic.server.room.service.usecase.UpdateRoomCapacityUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateRoomCapacityService implements UpdateRoomCapacityUseCase {

    private final RedisRoomQueryUseCase redisRoomQueryUseCase;
    private final RedisRoomCommandUseCase redisRoomCommandUseCase;

    @Override
    public RoomCapacityResponseDto update(Long memberId, String roomId, Integer roomCapacity) {

        RoomMember creator = redisRoomQueryUseCase.getCreator(roomId);

        validateCreatorId(memberId, creator);
        validateOverRoomCapacityWhenUpdateRoomCapacity(roomId, roomCapacity);

        redisRoomCommandUseCase.updateRoomCapacity(roomId, roomCapacity);

        return RoomCapacityResponseDto.builder()
                .roomId(roomId)
                .roomCapacity(roomCapacity)
                .build();
    }

    private void validateCreatorId (Long requestMemberId, RoomMember actualCreator) {
        if(!actualCreator.getMemberId().equals(requestMemberId)) {
            throw new WsException(WsErrorCode.ACCESS_DENIED_CREATOR_REQUIRED);
        }
    }

    private void validateOverRoomCapacityWhenUpdateRoomCapacity(String roomId, Integer roomCapacity) {
        int currentRoomMemberSize = redisRoomQueryUseCase.searchMember(roomId).size();

        if(currentRoomMemberSize > roomCapacity ) {
            throw new WsException(WsErrorCode.EXCEED_ROOM_CAPACITY);
        }
    }
}

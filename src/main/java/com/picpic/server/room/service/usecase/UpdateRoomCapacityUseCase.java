package com.picpic.server.room.service.usecase;

import com.picpic.server.room.dto.ws.RoomCapacityResponseDto;

public interface UpdateRoomCapacityUseCase {
    RoomCapacityResponseDto update(Long memberId, String roomId, Integer roomCapacity);
}

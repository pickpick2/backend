package com.picpic.server.room.dto.ws;

import lombok.Builder;

@Builder
public record RoomCapacityResponseDto(
        String roomId,
        Integer roomCapacity
) { }

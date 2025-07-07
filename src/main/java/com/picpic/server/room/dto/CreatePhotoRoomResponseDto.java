package com.picpic.server.room.dto;

import lombok.Builder;

@Builder
public record CreatePhotoRoomResponseDto(
    String roomId,
    Integer roomCapacity
) { }

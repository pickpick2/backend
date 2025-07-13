package com.picpic.server.room.dto;

public record UpdateStickerPositionRequestDTO(
        Long roomId,
        Long stickerInstanceId,
        Long stickerId,
        Integer x,
        Integer y
) {
}

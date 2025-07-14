package com.picpic.server.room.dto;

public record DeleteStickerRequestDTO(
        Long roomId,
        Long stickerInstanceId
) {
}

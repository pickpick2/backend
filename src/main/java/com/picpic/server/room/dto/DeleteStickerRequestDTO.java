package com.picpic.server.room.dto;

public record DeleteStickerRequestDTO(
        Long sessionId,
        String sessionCode,
        Long stickerInstanceId
) {
}

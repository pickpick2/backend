package com.picpic.server.room.dto;

public record DeletedStickerResponseDTO(
        String type,
        Long stickerInstanceId
) {
}

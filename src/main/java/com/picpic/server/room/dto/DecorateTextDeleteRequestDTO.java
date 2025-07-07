package com.picpic.server.room.dto;

public record DecorateTextDeleteRequestDTO(
        Long sessionId,
        String sessionCode,
        String textBoxId
) {
}

package com.picpic.server.room.dto;

public record DecorateTextDeleteRequestDTO(
        Long roomId,
        String textBoxId
) {
}

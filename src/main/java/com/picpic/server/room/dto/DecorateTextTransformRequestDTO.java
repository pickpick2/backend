package com.picpic.server.room.dto;

public record DecorateTextTransformRequestDTO(
        Long roomId,
        String textBoxId,
        Double scale,
        Integer rotate
) {
}

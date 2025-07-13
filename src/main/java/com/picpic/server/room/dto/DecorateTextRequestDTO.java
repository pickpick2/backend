package com.picpic.server.room.dto;

import java.util.List;

public record DecorateTextRequestDTO(
        Long roomId,
        String text,
        String font,
        Integer fontSize,
        String color,
        Integer x,
        Integer y
) {
}

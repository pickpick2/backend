package com.picpic.server.room.dto;

import java.util.List;

public record DecorateTextMoveRequestDTO(
        Long roomId,
        String textBoxId,
        Integer x,
        Integer y
) {
}

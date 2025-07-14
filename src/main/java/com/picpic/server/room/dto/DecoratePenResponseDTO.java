package com.picpic.server.room.dto;

import java.util.List;

public record DecoratePenResponseDTO(
        String type,
        Tool tool,
        String color,
        Integer strokeWidth,
        Integer x,
        Integer y
) {
}

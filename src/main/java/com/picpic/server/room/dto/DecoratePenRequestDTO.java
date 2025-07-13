package com.picpic.server.room.dto;

import java.util.List;

public record DecoratePenRequestDTO(
        Long roomId,
        Tool tool,
        String color,
        Integer strokeWidth,
        Integer x,
        Integer y
) {

}

package com.picpic.server.room.dto;

import java.util.List;

public record PenRedisDTO(
        Tool tool,
        String color,
        Integer strokeWidth,
        Integer x,
        Integer y
) {
}

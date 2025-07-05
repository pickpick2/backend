package com.picpic.server.room.dto;

import java.util.List;

public record DecoratePenResponseDTO(
        String type,
        String color,
        Integer lineWidth,
        List<Point> points,
        Tool tool
) {
    public record Point(
            int x,
            int y
    ) {}

}

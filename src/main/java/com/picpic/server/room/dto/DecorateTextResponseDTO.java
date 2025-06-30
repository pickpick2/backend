package com.picpic.server.room.dto;

import java.util.List;

public record DecorateTextResponseDTO(
        String text,
        String font,
        String color,
        List<Point> points
) {

    public record Point(
            int x,
            int y
    ) {
    }
}
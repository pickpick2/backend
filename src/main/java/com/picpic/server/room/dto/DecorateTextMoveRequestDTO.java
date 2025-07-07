package com.picpic.server.room.dto;

import java.util.List;

public record DecorateTextMoveRequestDTO(
        Long sessionId,
        String sessionCode,
        String textBoxId,
        List<Point> points
) {

    public record Point(
            int x,
            int y
    ) {
    }
}

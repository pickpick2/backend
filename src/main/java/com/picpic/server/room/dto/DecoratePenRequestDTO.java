package com.picpic.server.room.dto;

import java.util.List;

public record DecoratePenRequestDTO(
        Long sessionId,
        String sessionCode,
        Tool tool,
        String color,
        Integer lineWidth,
        List<Point> points
) {

    public record Point(
            int x,
            int y
    ) {
    }

}

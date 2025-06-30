
package com.picpic.server.room.dto;

import java.util.List;

public record DecorateStickerRequestDTO(
        Long sessionId,
        String sessionCode,
        Long stickerId,
        List<Point> points
) {

    public record Point(
            int x,
            int y
    ) {
    }
}
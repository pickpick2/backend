
package com.picpic.server.room.dto;

import java.util.List;

public record DecorateStickerRequestDTO(
        Long roomId,
        Long stickerId,
        List<Point> points
) {

    public record Point(
            int x,
            int y
    ) {
    }
}
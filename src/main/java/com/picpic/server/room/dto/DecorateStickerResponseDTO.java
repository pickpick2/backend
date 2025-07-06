package com.picpic.server.room.dto;

import java.util.List;

public record DecorateStickerResponseDTO(
        Long stickerInstanceId,
        Long stickerId,
        List<Point> points

) {

    public record Point(
            int x,
            int y
    ) {
    }
}
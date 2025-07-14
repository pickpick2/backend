package com.picpic.server.room.dto;

import java.util.List;

public record DecorateStickerResponseDTO(
        Long stickerInstanceId,
        Long stickerId,
        Integer x,
        Integer y,
        Integer width,
        Integer height,
        Double scale,
        Integer rotate

) {
}
package com.picpic.server.room.dto.ws;

public record DecorateStickerTransformRequestDTO(
        Long roomId,
        Long stickerInstanceId,
        Long stickerId,
        Double scale,
        Integer rotate
) {
}

package com.picpic.server.room.dto;

import java.util.List;

public record UpdateStickerPositionRequestDTO(
        Long roomId,
        Long stickerInstanceId,
        Long stickerId,
        List<DecorateStickerRequestDTO.Point> points
) {
}

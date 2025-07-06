package com.picpic.server.room.dto;

import java.util.List;

public record UpdateStickerPositionRequestDTO(
        Long sessionId,
        String sessionCode,
        Long stickerInstanceId,
        Long stickerId,
        List<DecorateStickerRequestDTO.Point> newPoints
) {
}

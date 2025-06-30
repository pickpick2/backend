package com.picpic.server.room.dto;

import java.util.List;

public record StickerRedisDTO(
        Long memberId,
        List<DecorateStickerRequestDTO.Point> points
) {
}

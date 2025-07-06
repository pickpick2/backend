package com.picpic.server.room.dto;

import java.io.Serializable;
import java.util.List;

public record StickerRedisDTO(
        Long stickerInstanceId,
        Long stickerId,
        Long memberId,
        List<DecorateStickerRequestDTO.Point> points
) implements Serializable {}

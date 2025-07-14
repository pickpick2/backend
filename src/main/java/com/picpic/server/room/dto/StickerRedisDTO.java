package com.picpic.server.room.dto;

import java.io.Serializable;
import java.util.List;

public record StickerRedisDTO(
        Long stickerInstanceId,
        Long stickerId,
        Long memberId,
        Integer x,
        Integer y,
        Integer width,
        Integer height,
        Integer scale,
        Integer rotate
) implements Serializable {}

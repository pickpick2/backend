package com.picpic.server.room.dto;

import java.time.Instant;

public record DecoreStartResponseDTO(
        Instant startTime,
        Integer duration
) {
}

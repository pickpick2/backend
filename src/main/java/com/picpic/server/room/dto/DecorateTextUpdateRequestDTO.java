package com.picpic.server.room.dto;

import java.util.List;

public record DecorateTextUpdateRequestDTO(
        Long sessionId,
        String sessionCode,
        String textBoxId,
        String newText,
        String newFont,
        String newColor

) {

}
package com.picpic.server.room.dto;

import java.util.List;

public record DecorateTextUpdateRequestDTO(
        Long roomId,
        String textBoxId,
        String newText,
        String newFont,
        Integer newFontSize,
        String newColor

) {

}
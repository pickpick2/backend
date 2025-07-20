
package com.picpic.server.room.dto;

import java.util.List;

public record DecorateStickerRequestDTO(
        Long roomId,
        Long stickerId,
        Integer x,
        Integer y,
        Integer width,
        Integer height

) {


}
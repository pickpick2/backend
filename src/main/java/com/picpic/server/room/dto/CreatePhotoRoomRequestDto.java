package com.picpic.server.room.dto;

import org.hibernate.validator.constraints.Range;

public record CreatePhotoRoomRequestDto (

        @Range(min = 1, max = 6)
        Integer roomCapacity
) { }

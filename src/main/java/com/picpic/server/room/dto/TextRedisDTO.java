package com.picpic.server.room.dto;
import java.io.Serializable;
import java.util.List;

public record TextRedisDTO(
        String textBoxId,
        String text,
        String font,
        Integer fontSize,
        String color,
        Integer x,
        Integer y
) implements Serializable {
}

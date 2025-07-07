package com.picpic.server.room.dto;
import java.io.Serializable;
import java.util.List;

public record TextRedisDTO(
        String textBoxId,
        String text,
        String font,
        String color,
        List<Point> points
) {
    public record Point(int x, int y) {}
}
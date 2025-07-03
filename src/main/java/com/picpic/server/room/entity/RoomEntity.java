package com.picpic.server.room.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@RedisHash(value = "room", timeToLive = 3600)
@AllArgsConstructor
public class RoomEntity {

    @Id
    private Long roomId;
    private String memberId;
}

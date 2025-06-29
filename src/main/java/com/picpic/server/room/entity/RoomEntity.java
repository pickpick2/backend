package com.picpic.server.room.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

@Builder
@RedisHash(value = "room", timeToLive = 3600)
@AllArgsConstructor
public class RoomEntity {

    @Id
    private String id;
    private String memberId;
}

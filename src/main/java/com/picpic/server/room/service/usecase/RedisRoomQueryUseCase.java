package com.picpic.server.room.service.usecase;

import com.picpic.server.room.domain.RoomMember;

import java.util.List;

public interface RedisRoomQueryUseCase {
    List<RoomMember> searchMember(String roomId);
    boolean exist(String roomId);
    Integer getRoomCapacity(String roomId);
    RoomMember getCreator(String roomId);
}

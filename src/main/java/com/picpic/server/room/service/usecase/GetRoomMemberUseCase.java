package com.picpic.server.room.service.usecase;

import com.picpic.server.room.domain.RoomMember;

import java.util.List;

public interface GetRoomMemberUseCase {
    List<RoomMember> getRoomMember(Long memberId, String roomId);
}

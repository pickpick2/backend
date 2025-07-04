package com.picpic.server.room.service.usecase;

import java.util.Set;

public interface GetRoomMemberUseCase {
    Set<Object> getRoomMember(Long memberId, String roomId);
}

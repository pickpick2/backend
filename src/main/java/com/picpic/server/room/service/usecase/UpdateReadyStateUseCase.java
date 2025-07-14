package com.picpic.server.room.service.usecase;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.ws.UpdateReadyStateRequestDto;

public interface UpdateReadyStateUseCase {
	RoomMember update(String roomId, long memberId, UpdateReadyStateRequestDto request);
}

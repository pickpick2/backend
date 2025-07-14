package com.picpic.server.room.service;

import org.springframework.stereotype.Service;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.ws.UpdateReadyStateRequestDto;
import com.picpic.server.room.enums.RoomMemberStatus;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import com.picpic.server.room.service.usecase.UpdateReadyStateUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateReadyStateService implements UpdateReadyStateUseCase {

	private final RedisRoomCommandUseCase redisRoomCommandUseCase;

	@Override
	public RoomMember update(String roomId, long memberId, UpdateReadyStateRequestDto request) {

		RoomMemberStatus requestState = RoomMemberStatus.valueOf(request.memberReadyState().name());

		return redisRoomCommandUseCase.updateReadyState(roomId, memberId, requestState);
	}
}

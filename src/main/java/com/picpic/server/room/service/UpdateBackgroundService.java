package com.picpic.server.room.service;

import org.springframework.stereotype.Service;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;
import com.picpic.server.room.service.usecase.UpdateBackgroundUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateBackgroundService implements UpdateBackgroundUseCase {

	private final RedisRoomCommandUseCase redisRoomCommandUseCase;
	private final RedisRoomQueryUseCase redisRoomQueryUseCase;

	@Override
	public Integer update(String roomId, Long memberId, Integer backgroundId) {

		RoomMember creator = redisRoomQueryUseCase.getCreator(roomId);

		validateCreatorId(memberId, creator);

		return redisRoomCommandUseCase.updateBackground(roomId, backgroundId);
	}

	private void validateCreatorId (Long requestMemberId, RoomMember actualCreator) {
		if(!actualCreator.getMemberId().equals(requestMemberId)) {
			throw new WsException(WsErrorCode.ACCESS_DENIED_CREATOR_REQUIRED);
		}
	}
}

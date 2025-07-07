package com.picpic.server.room.service;

import org.springframework.stereotype.Service;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.util.IdUtils;
import com.picpic.server.room.service.usecase.CreateRoomUseCase;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;
import com.picpic.server.room.service.usecase.RoomHistoryCommandUseCase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRoomService implements CreateRoomUseCase {

	private final RedisRoomCommandUseCase redisRoomCommandUseCase;
	private final RoomHistoryCommandUseCase roomHistoryCommandUseCase;

	@Override
	public String createRoom(MemberPrincipalDetail creatorPrinciple, Integer roomCapacity) {
		String roomId = IdUtils.generateRoomId();

		roomHistoryCommandUseCase.create(IdUtils.generateTsid(), creatorPrinciple.memberId());
		redisRoomCommandUseCase.create(roomId, creatorPrinciple, roomCapacity);

		return roomId;
	}
}

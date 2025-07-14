package com.picpic.server.room.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.enums.RoomMemberStatus;
import com.picpic.server.room.service.usecase.ChangePageUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChangePageService implements ChangePageUseCase {

	private final RedisRoomQueryUseCase redisRoomQueryUseCase;

	@Override
	public void validate(String roomId, Long memberId) {

		RoomMember creator = redisRoomQueryUseCase.getCreator(roomId);
		List<RoomMember> roomMembers = redisRoomQueryUseCase.searchMember(roomId);

		validateCreatorId(memberId, creator);
		validateAllReadyState(roomMembers);
	}

	private void validateCreatorId (Long requestMemberId, RoomMember actualCreator) {
		if(!actualCreator.getMemberId().equals(requestMemberId)) {
			throw new WsException(WsErrorCode.ACCESS_DENIED_CREATOR_REQUIRED);
		}
	}

	private void validateAllReadyState (List<RoomMember> roomMembers) {
		long count = roomMembers.stream().filter(member ->
			member.getMemberStatus() != RoomMemberStatus.READY
		).count();

		if(count > 0) {
			throw new WsException(WsErrorCode.NOT_ALL_READY);
		}
	}
}

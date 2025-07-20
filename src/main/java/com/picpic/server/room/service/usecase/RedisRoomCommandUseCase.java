package com.picpic.server.room.service.usecase;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.enums.RoomMemberStatus;

public interface RedisRoomCommandUseCase {
	void create(String roomId, MemberPrincipalDetail memberPrincipal, Integer roomCapacity);

	void delete(String roomId);

	void addMember(String roomId, MemberPrincipalDetail memberPrincipalDetail);

	void subtractMember(String roomId, Long memberId);

	void updateRoomCapacity(String roomId, Integer roomCapacity);

	RoomMember updateReadyState(String roomId, Long memberId, RoomMemberStatus roomMemberStatus);

	int updateBackground(String roomId, Integer backgroundId);
}

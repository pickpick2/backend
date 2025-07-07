package com.picpic.server.room.service.usecase;

import com.picpic.server.common.auth.MemberPrincipalDetail;

public interface RedisRoomCommandUseCase {
	void create(String roomId, MemberPrincipalDetail memberPrincipal, Integer roomCapacity);

	void delete(String roomId);

	void addMember(String roomId, MemberPrincipalDetail memberPrincipalDetail);

	void subtractMember(String roomId, Long memberId);

	void updateRoomCapacity(String roomId, Integer roomCapacity);
}

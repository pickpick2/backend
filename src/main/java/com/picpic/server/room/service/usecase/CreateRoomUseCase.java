package com.picpic.server.room.service.usecase;

import com.picpic.server.common.auth.MemberPrincipalDetail;

public interface CreateRoomUseCase {
	String createRoom(MemberPrincipalDetail creatorPrinciple, Integer roomCapacity);
}

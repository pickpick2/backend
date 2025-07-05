package com.picpic.server.room.service.usecase;

import com.picpic.server.common.security.MemberPrincipalDetail;

public interface CreateRoomUseCase {
    String createRoom(MemberPrincipalDetail creatorPrinciple);
}

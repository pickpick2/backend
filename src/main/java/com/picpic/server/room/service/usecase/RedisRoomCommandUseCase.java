package com.picpic.server.room.service.usecase;

import com.picpic.server.common.security.MemberPrincipalDetail;

public interface RedisRoomCommandUseCase {
    void create(String roomId, MemberPrincipalDetail memberPrincipal);
    void delete(String roomId);
    void addMember(String roomId, MemberPrincipalDetail memberPrincipalDetail);
    void subtractMember(String roomId, Long memberId);
}

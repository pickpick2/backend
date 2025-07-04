package com.picpic.server.room.controller;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GetRoomMemberController {

    private final GetRoomMemberUseCase getRoomMemberUseCase;

    @GetMapping("/room/{roomId}/member")
    public Set<Object> getRoom(
            @AuthenticationPrincipal MemberPrincipalDetail memberDetail,
            @PathVariable String roomId
    ) {
        Set<Object> roomMember = getRoomMemberUseCase.getRoomMember(memberDetail.memberId(), roomId);

        return roomMember;
    }
}

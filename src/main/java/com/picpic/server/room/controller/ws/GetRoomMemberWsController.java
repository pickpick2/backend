package com.picpic.server.room.controller.ws;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GetRoomMemberWsController {

    private final GetRoomMemberUseCase getRoomMemberUseCase;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/room/{roomId}/members")
    @SendToUser("/topic/room/members")
    public Set<Object> getRoom(
            @AuthenticationPrincipal MemberPrincipalDetail memberDetail,
            @DestinationVariable String roomId
    ) {
        return getRoomMemberUseCase.getRoomMember(memberDetail.memberId(), roomId);
    }
}

package com.picpic.server.room.controller.ws;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.service.usecase.GetRoomMemberUseCase;
import com.picpic.server.room.service.usecase.RedisRoomQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GetRoomMemberWsController {

    private final GetRoomMemberUseCase getRoomMemberUseCase;

    @MessageMapping("/room/{roomId}/members")
    @SendToUser("/topic/room/members")
    public List<RoomMember> getRoom(
            @AuthenticationPrincipal MemberPrincipalDetail memberDetail,
            @DestinationVariable String roomId
    ) {
        List<RoomMember> roomMember = getRoomMemberUseCase.getRoomMember(memberDetail.memberId(), roomId);

        return roomMember;
    }
}

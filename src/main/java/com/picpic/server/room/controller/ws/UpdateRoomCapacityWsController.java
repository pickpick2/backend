package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.response.WsResponse;
import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.room.dto.ws.RoomCapacityRequestDto;
import com.picpic.server.room.dto.ws.RoomCapacityResponseDto;
import com.picpic.server.room.service.usecase.UpdateRoomCapacityUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UpdateRoomCapacityWsController {

    private final UpdateRoomCapacityUseCase updateRoomCapacityUseCase;

    @MessageMapping("/room/{roomId}/capacity")
    @SendTo("/topic/room/{roomId}")
    public WsResponse<RoomCapacityResponseDto> updateRoomCapacity(
            @AuthenticationPrincipal MemberPrincipalDetail memberDetail,
            @DestinationVariable String roomId,
            RoomCapacityRequestDto request
    ) {
        RoomCapacityResponseDto response = updateRoomCapacityUseCase.update(
            memberDetail.memberId(),
            roomId,
            request.roomCapacity()
        );

        return WsResponse.success("ROOM_CAPACITY", response);
    }
}

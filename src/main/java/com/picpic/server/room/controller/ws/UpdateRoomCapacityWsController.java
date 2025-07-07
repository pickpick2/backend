package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.auth.MemberPrincipalDetail;
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
	@SendTo("/topic/room/{roomId}/capacity")
	public RoomCapacityResponseDto updateRoomCapacity(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId,
		RoomCapacityRequestDto request
	) {
		return updateRoomCapacityUseCase.update(memberDetail.memberId(), roomId, request.roomCapacity());
	}
}

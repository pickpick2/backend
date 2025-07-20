package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.response.WsResponse;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.RoomMemberResponseDto;
import com.picpic.server.room.dto.ws.UpdateReadyStateRequestDto;
import com.picpic.server.room.service.usecase.UpdateReadyStateUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UpdateReadyStateWsController {

	private final UpdateReadyStateUseCase updateReadyStateUseCase;

	@MessageMapping("/room/{roomId}/member/state")
	@SendTo("/topic/room/{roomId}")
	public WsResponse<RoomMemberResponseDto> update(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId,
		UpdateReadyStateRequestDto request
	) {

		RoomMember result = updateReadyStateUseCase.update(roomId, memberDetail.memberId(), request);

		RoomMemberResponseDto response = RoomMemberResponseDto.from(result);

		return WsResponse.success("MEMBER_STATE", response);
	}
}

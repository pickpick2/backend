package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.response.WsResponse;
import com.picpic.server.room.dto.ws.UpdateBackgroundRequestDto;
import com.picpic.server.room.dto.ws.UpdateBackgroundResponseDto;
import com.picpic.server.room.service.usecase.UpdateBackgroundUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UpdateBackgroundWsController {

	private final UpdateBackgroundUseCase updateBackgroundUseCase;

	@MessageMapping("/room/{roomId}/background")
	@SendTo("/topic/room/{roomId}")
	public WsResponse<UpdateBackgroundResponseDto> updateBackground(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId,
		UpdateBackgroundRequestDto request
	) {
		Integer update = updateBackgroundUseCase.update(
			roomId,
			memberDetail.memberId(),
			request.backgroundId());

		return WsResponse.success("UPDATE_BACKGROUND", UpdateBackgroundResponseDto.of(update));
	}
}

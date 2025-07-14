package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.response.WsResponse;
import com.picpic.server.room.dto.ws.ChangePageRequestDto;
import com.picpic.server.room.dto.ws.ChangePageResponseDto;
import com.picpic.server.room.service.usecase.ChangePageUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChangePageWsController {

	private final ChangePageUseCase changePageUseCase;

	@MessageMapping("/room/{roomId}/page")
	@SendTo("/topic/room/{roomId}")
	public WsResponse<ChangePageResponseDto> update(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId,
		ChangePageRequestDto request
	) {
		changePageUseCase.validate(roomId, memberDetail.memberId());

		return WsResponse.success("CHANGE_PAGE", ChangePageResponseDto.of(request.page()));
	}
}

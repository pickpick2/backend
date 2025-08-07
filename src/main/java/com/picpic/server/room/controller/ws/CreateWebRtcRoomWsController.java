package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.response.WsResponse;
import com.picpic.server.room.dto.CreateRtcRoomResponseDto;
import com.picpic.server.room.service.usecase.CreateWebRtcRoomUseCase;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CreateWebRtcRoomWsController {

	private final CreateWebRtcRoomUseCase createWebRtcRoomUseCase;

	@MessageMapping("/room/{roomId}/rtc/room")
	@SendTo("/topic/room/{roomId}")
	public WsResponse<CreateRtcRoomResponseDto> update(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId
	) throws OpenViduJavaClientException, OpenViduHttpException {
		String createdRtcRoomId = createWebRtcRoomUseCase.createRoom(memberDetail.memberId(), roomId);

		return WsResponse.success("CREATE_RTC_ROOM", CreateRtcRoomResponseDto.of(createdRtcRoomId));
	}
}

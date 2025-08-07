package com.picpic.server.room.controller.ws;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.picpic.server.common.auth.MemberPrincipalDetail;
import com.picpic.server.common.response.WsResponse;
import com.picpic.server.room.dto.CreateRtcTokenResponseDto;
import com.picpic.server.room.service.usecase.CreateWebRtcTokenUseCase;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CreateWebRtcTokenWsController {

	private final CreateWebRtcTokenUseCase createWebRtcTokenUseCase;

	@MessageMapping("/room/{roomId}/rtc/token")
	@SendToUser("/queue/room")
	public WsResponse<CreateRtcTokenResponseDto> create(
		@AuthenticationPrincipal MemberPrincipalDetail memberDetail,
		@DestinationVariable String roomId
	) throws OpenViduJavaClientException, OpenViduHttpException {
		String createdRtcTokenURI = createWebRtcTokenUseCase.createToken(roomId, memberDetail);

		return WsResponse.success("CREATE_RTC_TOKEN", CreateRtcTokenResponseDto.of(createdRtcTokenURI));
	}
}

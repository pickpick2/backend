package com.picpic.server.frame.controller.ws;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.picpic.server.frame.dto.ws.FrameActionRequest;
import com.picpic.server.frame.dto.ws.FrameWsMessage;
import com.picpic.server.frame.service.ws.FrameActionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FrameWsController {

	private final FrameActionService frameActionService;

	/**
	 * 클라이언트가 보낼 때 destination: /app/frame/action
	 */
	@MessageMapping("/frame/action")
	public void handleFrameAction(
		@Header("roomId") String roomId,
		@Payload FrameWsMessage<FrameActionRequest> msg
	) {
		frameActionService.handleAction(
			roomId,
			msg.getData().getUserId(),
			msg.getData().getFrameId(),
			msg.getType(),
			msg.getRequestId()
		);
	}
}
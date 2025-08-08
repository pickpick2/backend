package com.picpic.server.frame.controller.ws;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.picpic.server.frame.dto.ws.FrameActionRequest;
import com.picpic.server.frame.dto.ws.FrameWsMessage;
import com.picpic.server.frame.service.ws.FrameActionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FrameWsController {

	private final FrameActionService frameActionService;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 투표 단계 시작을 WebSocket으로 수신
	 */
	@MessageMapping("/frame/start-vote")
	public void startVote(@Header("roomId") String roomId) {
		frameActionService.startVote(roomId);
	}

	/**
	 * 셀 선택 단계 시작을 WebSocket으로 수신
	 */
	@MessageMapping("/frame/start-cell")
	public void startCell(@Header("roomId") String roomId) {
		frameActionService.startCell(roomId);
	}

	/**
	 * 기존 액션(VOTE, SELECT) 위임
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
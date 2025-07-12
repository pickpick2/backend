package com.picpic.server.common.config.WebSocket.interceptor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpic.server.common.exception.WsException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
		StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
		errorAccessor.setLeaveMutable(true);

		Map<String, Object> response = new HashMap<>();

		WsException wsException = null;

		Throwable cause = ex.getCause();

		if(cause instanceof WsException) {
			wsException = (WsException)cause;
		}

		if(wsException != null) {
			response.put("type", wsException.getErrorCode().getCode());
			response.put("message", wsException.getMessage());
		}

		try {
			byte[] payload = objectMapper.writeValueAsBytes(response);

			return MessageBuilder.createMessage(payload, errorAccessor.getMessageHeaders());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return super.handleClientMessageProcessingError(clientMessage, ex);
	}
}

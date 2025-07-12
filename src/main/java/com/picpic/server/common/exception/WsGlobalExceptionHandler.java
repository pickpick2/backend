package com.picpic.server.common.exception;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.picpic.server.common.response.WsResponse;

@ControllerAdvice
public class WsGlobalExceptionHandler {

	@MessageExceptionHandler
	@SendToUser("/queue/errors")
	public WsResponse<String> handleValidationException(WsException exception) {
		return WsResponse.error(exception);
	}

}

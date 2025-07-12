package com.picpic.server.common.exception;

import lombok.Getter;

@Getter
public class WsException extends RuntimeException {

	private final WsErrorCode errorCode;

	public WsException(WsErrorCode code) {
		super(code.getMessage());
		this.errorCode = code;
	}
}

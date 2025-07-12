package com.picpic.server.common.response;

import com.picpic.server.common.exception.WsException;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WsResponse<T> {

	private String type;
	private T data;

	public static <T> WsResponse<T> success(String type, T data) {
		return WsResponse.<T>builder()
			.type(type)
			.data(data)
			.build();
	}

	public static WsResponse<String> error(WsException exception) {
		return WsResponse.<String>builder()
			.type(exception.getErrorCode().getCode())
			.data(exception.getMessage())
			.build();
	}
}

package com.picpic.server.frame.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * STOMP 공통 메시지 래퍼
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrameWsMessage<T> {
	private FrameMessageType type;
	private String requestId;
	private T data;
}
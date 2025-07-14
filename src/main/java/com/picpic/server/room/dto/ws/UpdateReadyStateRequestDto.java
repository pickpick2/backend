package com.picpic.server.room.dto.ws;

public record UpdateReadyStateRequestDto (
	MemberReadyState memberReadyState
) {
	public enum MemberReadyState {
		READY, NOT_READY
	}
}

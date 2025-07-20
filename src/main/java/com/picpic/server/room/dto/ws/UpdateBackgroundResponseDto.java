package com.picpic.server.room.dto.ws;


public record UpdateBackgroundResponseDto(
	Integer backgroundId
) {
	public static UpdateBackgroundResponseDto of(Integer backgroundId) {
		return new UpdateBackgroundResponseDto(backgroundId);
	}
}

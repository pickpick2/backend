package com.picpic.server.room.dto;

public record CreateRtcRoomResponseDto (
	String rtcRoomId
) {
	public static CreateRtcRoomResponseDto of(String rtcRoomId) {
		return new CreateRtcRoomResponseDto(rtcRoomId);
	}
}

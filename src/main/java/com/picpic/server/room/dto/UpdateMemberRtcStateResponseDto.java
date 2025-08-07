package com.picpic.server.room.dto;

import lombok.Builder;

@Builder
public record UpdateMemberRtcStateResponseDto (
	String nickname,
	MemberRtcState state
) {
	public enum MemberRtcState {
		CONNECTED, DISCONNECTED
	}

	public static UpdateMemberRtcStateResponseDto of(String nickname, MemberRtcState state) {
		return UpdateMemberRtcStateResponseDto.builder()
			.nickname(nickname)
			.state(state)
			.build();
	}
}

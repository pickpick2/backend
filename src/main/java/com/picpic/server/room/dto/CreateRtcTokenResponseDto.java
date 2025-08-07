package com.picpic.server.room.dto;

import lombok.Builder;

@Builder
public record CreateRtcTokenResponseDto(
	String sessionId,
	String token
) {
	public static CreateRtcTokenResponseDto of(String rtcToken) {
		CreateRtcTokenResponseDtoBuilder builder = CreateRtcTokenResponseDto.builder();

		String[] splitArr = rtcToken.split("\\?")[1].split("&");

		for(String split: splitArr) {
			String[] keyValue = split.split("=");
			if(keyValue.length == 2) {
				if ("sessionId".equals(keyValue[0])) {
					builder.sessionId(keyValue[1]);
				} else if ("token".equals(keyValue[0])) {
					builder.token(keyValue[1]);
				}
			}
		}

		return builder.build();
	}
}

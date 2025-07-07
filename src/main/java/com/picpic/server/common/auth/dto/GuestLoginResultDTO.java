package com.picpic.server.common.auth.dto;

public record GuestLoginResultDTO(
	Long memberId,
	String accessToken) {
}

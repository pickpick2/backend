package com.picpic.server.room.dto.ws;

import com.picpic.server.room.enums.PageEnum;

import lombok.Builder;

@Builder
public record ChangePageResponseDto(
	PageEnum page
) {
	public static ChangePageResponseDto of(PageEnum page) {
		return ChangePageResponseDto.builder()
			.page(page)
			.build();
	}
}

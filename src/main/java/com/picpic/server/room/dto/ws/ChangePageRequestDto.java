package com.picpic.server.room.dto.ws;

import com.picpic.server.room.enums.PageEnum;

public record ChangePageRequestDto(
	PageEnum page
) {
}

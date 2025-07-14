package com.picpic.server.room.service.usecase;

public interface UpdateBackgroundUseCase {
	Integer update(String roomId, Long memberId, Integer backgroundId);
}

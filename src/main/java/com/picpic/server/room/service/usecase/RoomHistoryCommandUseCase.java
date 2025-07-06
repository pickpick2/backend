package com.picpic.server.room.service.usecase;

public interface RoomHistoryCommandUseCase {
    void create(Long roomHistoryId, Long creatorId);
}

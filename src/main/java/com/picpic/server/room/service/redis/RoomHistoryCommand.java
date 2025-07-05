package com.picpic.server.room.service.redis;

import com.picpic.server.room.entity.RoomHistoryEntity;
import com.picpic.server.room.repository.RoomHistoryRepository;
import com.picpic.server.room.service.usecase.RoomHistoryCommandUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomHistoryCommand implements RoomHistoryCommandUseCase {

    private final RoomHistoryRepository roomHistoryRepository;

    @Override
    public void create(Long roomHistoryId, Long creatorId) {

        RoomHistoryEntity roomHistory = RoomHistoryEntity.builder()
                .roomHistoryId(roomHistoryId)
                .memberId(creatorId)
                .build();

        roomHistoryRepository.save(roomHistory);
    }
}

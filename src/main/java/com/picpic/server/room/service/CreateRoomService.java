package com.picpic.server.room.service;

import com.picpic.server.common.util.IdUtils;
import com.picpic.server.room.entity.RoomEntity;
import com.picpic.server.room.entity.RoomHistoryEntity;
import com.picpic.server.room.repository.RoomHistoryRepository;
import com.picpic.server.room.repository.RoomRepository;
import com.picpic.server.room.service.usecase.CreateRoomUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateRoomService implements CreateRoomUseCase {

    private final RoomHistoryRepository roomHistoryRepository;
    private final RoomRepository roomRepository;

    @Override
    public String createRoom(long memberId) {

        String roomId = UUID.randomUUID().toString();

        roomRepository.save(RoomEntity.builder()
                        .id(roomId)
                        .memberId(String.valueOf(memberId))
                        .build());

        roomHistoryRepository.save(
                RoomHistoryEntity.builder()
                        .roomHistoryId(IdUtils.generateId())
                        .memberId(memberId)
                        .build());

        return roomId;
    }
}

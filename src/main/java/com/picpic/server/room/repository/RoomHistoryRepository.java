package com.picpic.server.room.repository;

import com.picpic.server.room.entity.RoomHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomHistoryRepository extends JpaRepository<RoomHistoryEntity,Long> {
}

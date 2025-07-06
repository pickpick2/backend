package com.picpic.server.room.repository;

import com.picpic.server.room.entity.RoomRedisEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRedisRepository extends CrudRepository<RoomRedisEntity, String> {

}

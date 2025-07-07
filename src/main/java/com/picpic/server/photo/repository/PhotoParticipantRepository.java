package com.picpic.server.photo.repository;

import com.picpic.server.photo.entity.PhotoParticipantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoParticipantRepository extends JpaRepository<PhotoParticipantEntity, Long> {
}

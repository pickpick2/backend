package com.picpic.server.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.picpic.server.photo.entity.PhotoParticipantEntity;

public interface PhotoParticipantRepository extends JpaRepository<PhotoParticipantEntity, Long> {
}

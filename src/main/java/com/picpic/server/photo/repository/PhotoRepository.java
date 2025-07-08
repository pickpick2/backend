package com.picpic.server.photo.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.picpic.server.photo.entity.PhotoEntity;

public interface PhotoRepository extends JpaRepository<PhotoEntity, String> {
	void deleteByPhotoId(String photoId);

	Page<PhotoEntity> findByCreatedAtBefore(Instant before, Pageable pageable);

	Page<PhotoEntity> findByTitleContainingAndCreatedAtBefore(
		String title, Instant before, Pageable pageable);
}

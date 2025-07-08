package com.picpic.server.photo.repository;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.picpic.server.photo.entity.PhotoAlbumEntity;
import com.picpic.server.photo.entity.PhotoAlbumId;

public interface PhotoAlbumRepository extends JpaRepository<PhotoAlbumEntity, PhotoAlbumId> {
	void deleteByIdPhotoIdAndIdMemberId(String photoId, long memberId);

	boolean existsByIdPhotoId(String photoId);

	Page<PhotoAlbumEntity> findByIdMemberIdAndPhotoCreatedAtBefore(
		long memberId, Instant cursor, Pageable pageable);

	Page<PhotoAlbumEntity> findByIdMemberIdAndPhotoTitleContainingAndPhotoCreatedAtBefore(
		long memberId, String title, Instant cursor, Pageable pageable);
}

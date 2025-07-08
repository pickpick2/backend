package com.picpic.server.photo.service.usecase;

import java.time.Instant;
import java.util.List;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;

public interface PhotoUseCase {
	List<PhotoListResponse> listPhotos(String search, Instant cursorCreatedAt, int size);

	PhotoDetailResponse getPhotoDetail(String photoId, long memberId);

	void deletePhoto(String photoId, long memberId);
}

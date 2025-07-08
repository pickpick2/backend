package com.picpic.server.photo.service.usecase;

import java.time.Instant;
import java.util.List;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;

public interface PhotoUseCase {
	/**
	 *
	 * @param memberId          조회할 회원 ID
	 * @param search            검색어 (옵션)
	 * @param cursorCreatedAt   ISO-8601 포맷 커서(생성일시). null 이면 최신부터.
	 * @param size              가져올 개수
	 * @return PhotoListResponse 리스트
	 */
	List<PhotoListResponse> listPhotos(long memberId, String search, Instant cursorCreatedAt, int size);

	PhotoDetailResponse getPhotoDetail(String photoId, long memberId);

	void deletePhoto(String photoId, long memberId);
}

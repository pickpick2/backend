package com.picpic.server.photo.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.entity.PhotoEntity;
import com.picpic.server.photo.entity.PhotoParticipantEntity;
import com.picpic.server.photo.repository.PhotoAlbumRepository;
import com.picpic.server.photo.repository.PhotoRepository;
import com.picpic.server.photo.service.usecase.PhotoUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoService implements PhotoUseCase {

	private final PhotoRepository photoRepo;
	private final PhotoAlbumRepository albumRepo;

	@Override
	@Transactional(readOnly = true)
	public List<PhotoListResponse> listPhotos(
		long memberId, String search, Instant cursorCreatedAt, int size) {

		// 커서 기반 페이징: photo.createdAt 내림차순
		PageRequest pageReq = PageRequest.of(0, size,
			Sort.by(Sort.Direction.DESC, "photo.createdAt"));

		var page = (search == null || search.isBlank())
			? albumRepo.findByIdMemberIdAndPhotoCreatedAtBefore(memberId, cursorCreatedAt, pageReq)
			: albumRepo.findByIdMemberIdAndPhotoTitleContainingAndPhotoCreatedAtBefore(
			memberId, search, cursorCreatedAt, pageReq);

		return page.stream()
			.map(album -> {
				PhotoEntity photo = album.getPhoto();
				return PhotoListResponse.builder()
					.photoId(photo.getPhotoId())
					.title(photo.getTitle())
					.url(photo.getUrl())
					.createdAt(photo.getCreatedAt().toEpochMilli())
					.build();
			})
			.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public PhotoDetailResponse getPhotoDetail(String photoId, long memberId) {
		// 1) 내 앨범에 담겨 있는지 확인
		var albumId = new com.picpic.server.photo.entity.PhotoAlbumId(photoId, memberId);
		albumRepo.findById(albumId)
			.orElseThrow(() -> new IllegalArgumentException("앨범에 포함되지 않은 사진입니다."));

		// 2) 사진과 원본 참여자 로드
		PhotoEntity photo = photoRepo.findById(photoId)
			.orElseThrow(() -> new IllegalArgumentException("사진이 존재하지 않습니다."));

		List<Long> participants = photo.getParticipants().stream()
			.map(PhotoParticipantEntity::getMemberId)
			.distinct()
			.collect(Collectors.toList());

		return PhotoDetailResponse.builder()
			.photoId(photo.getPhotoId())
			.title(photo.getTitle())
			.url(photo.getUrl())
			.createdAt(photo.getCreatedAt().toEpochMilli())
			.participantIds(participants)
			.build();
	}

	@Override
	@Transactional
	public void deletePhoto(String photoId, long memberId) {
		// 1) 내 앨범 연결만 삭제
		albumRepo.deleteByIdPhotoIdAndIdMemberId(photoId, memberId);

		// 2) 남은 사용자가 없으면 실제 사진 삭제
		boolean stillInUse = albumRepo.existsByIdPhotoId(photoId);
		if (!stillInUse) {
			photoRepo.deleteById(photoId);
		}
	}
}

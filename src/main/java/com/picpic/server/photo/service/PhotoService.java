package com.picpic.server.photo.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.entity.PhotoEntity;
import com.picpic.server.photo.entity.PhotoParticipantEntity;
import com.picpic.server.photo.repository.PhotoParticipantRepository;
import com.picpic.server.photo.repository.PhotoRepository;
import com.picpic.server.photo.service.usecase.PhotoUseCase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService implements PhotoUseCase {

	private final PhotoRepository photoRepo;
	private final PhotoParticipantRepository participantRepo;

	@Override
	public List<PhotoListResponse> listPhotos(
		String search, Instant cursorCreatedAt, int size) {

		// PageRequest: createdAt 기준 내림차순
		PageRequest pageReq = PageRequest.of(0, size,
			Sort.by(Sort.Direction.DESC, "createdAt"));

		List<PhotoEntity> entities;

		if (search == null || search.isBlank()) {
			// cursorCreatedAt 이전 데이터만 조회
			entities = photoRepo
				.findByCreatedAtBefore(cursorCreatedAt, pageReq)
				.getContent();
		} else {
			// 검색 + 커서 결합: title 포함 + createdAt 이전
			entities = photoRepo
				.findByTitleContainingAndCreatedAtBefore(
					search, cursorCreatedAt, pageReq)
				.getContent();
		}

		return entities.stream()
			.map(p -> PhotoListResponse.builder()
				.photoId(p.getPhotoId())
				.title(p.getTitle())
				.url(p.getUrl())
				.createdAt(p.getCreatedAt().toEpochMilli())
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public PhotoDetailResponse getPhotoDetail(String photoId, long memberId) {
		PhotoEntity photo = photoRepo.findById(photoId)
			.orElseThrow(() -> new IllegalArgumentException("사진이 존재하지 않습니다."));

		var participants = photo.getParticipants().stream()
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
	public void deletePhoto(String photoId, long memberId) {
		// TODO: memberId 검증 (사진 소유자 또는 참여자만 삭제 가능)
		photoRepo.deleteByPhotoId(photoId);
	}
}

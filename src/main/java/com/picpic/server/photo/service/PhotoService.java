package com.picpic.server.photo.service;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.dto.PhotoDetailResponse.PhotoDetailResponseBuilder;
import com.picpic.server.photo.entity.PhotoEntity;
import com.picpic.server.photo.entity.PhotoParticipantEntity;
import com.picpic.server.photo.repository.PhotoParticipantRepository;
import com.picpic.server.photo.repository.PhotoRepository;
import com.picpic.server.photo.service.usecase.PhotoUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PhotoService implements PhotoUseCase {

    private final PhotoRepository photoRepo;
    private final PhotoParticipantRepository participantRepo;

    @Override
    public Page<PhotoListResponse> listPhotos(String search, String sort, Pageable pageable) {
        Sort.Direction dir = "desc".equalsIgnoreCase(sort) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pg = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(dir, "createdAt"));

        Page<PhotoEntity> page = (search == null || search.isBlank())
                ? photoRepo.findAll(pg)
                : photoRepo.findAll(Example.of(
                PhotoEntity.builder().title(search).build(),
                ExampleMatcher.matchingAny()
                        .withMatcher("title", m -> m.contains())
        ), pg);

        return page.map(p -> PhotoListResponse.builder()
                .photoId(p.getPhotoId())
                .title(p.getTitle())
                .url(p.getUrl())
                .createdAt(p.getCreatedAt().toEpochMilli())
                .build());
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

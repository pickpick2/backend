package com.picpic.server.photo.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.entity.PhotoAlbumEntity;
import com.picpic.server.photo.entity.PhotoAlbumId;
import com.picpic.server.photo.entity.PhotoEntity;
import com.picpic.server.photo.entity.PhotoParticipantEntity;
import com.picpic.server.photo.repository.PhotoAlbumRepository;
import com.picpic.server.photo.repository.PhotoRepository;

class PhotoServiceTest {

	@Mock
	PhotoRepository photoRepo;
	@Mock
	PhotoAlbumRepository albumRepo;

	@InjectMocks
	PhotoService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void listPhotos_NoSearch_ReturnsDtos() {
		// given
		long memberId = 1L;
		Instant cursor = Instant.now();
		PageRequest pr = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "photo.createdAt"));

		PhotoEntity e1 = PhotoEntity.builder()
			.photoId("p1").title("T1").url("u1").createdAt(Instant.now()).build();
		PhotoAlbumEntity a1 = PhotoAlbumEntity.of("p1", memberId);
		// inject PhotoEntity into album
		ReflectionTestUtils.setField(a1, "photo", e1);

		Page<PhotoAlbumEntity> page = new PageImpl<>(List.of(a1));
		given(albumRepo.findByIdMemberIdAndPhotoCreatedAtBefore(memberId, cursor, pr))
			.willReturn(page);

		// when
		List<PhotoListResponse> dtos = service.listPhotos(memberId, "", cursor, 2);

		// then
		assertThat(dtos).hasSize(1);
		assertThat(dtos.get(0).getPhotoId()).isEqualTo("p1");
	}

	@Test
	void getPhotoDetail_Found_ReturnsDetail() {
		// given
		String pid = "pX";
		long memberId = 42L;
		PhotoEntity e = PhotoEntity.builder()
			.photoId(pid).title("TT").url("UU").createdAt(Instant.now()).build();
		PhotoParticipantEntity pp = PhotoParticipantEntity.builder()
			.memberId(memberId).build();
		ReflectionTestUtils.setField(pp, "photo", e);
		// participants 리스트 세팅
		ReflectionTestUtils.setField(e, "participants", List.of(pp));

		given(albumRepo.findById(new PhotoAlbumId(pid, memberId)))
			.willReturn(Optional.of(PhotoAlbumEntity.of(pid, memberId)));
		given(photoRepo.findById(pid)).willReturn(Optional.of(e));

		// when
		PhotoDetailResponse detail = service.getPhotoDetail(pid, memberId);

		// then
		assertThat(detail.getPhotoId()).isEqualTo(pid);
		assertThat(detail.getParticipantIds()).contains(memberId);
	}

	@Test
	void deletePhoto_LastUser_DeletesEntity() {
		// given
		String pid = "pZ";
		long memberId = 7L;

		willDoNothing().given(albumRepo).deleteByIdPhotoIdAndIdMemberId(pid, memberId);
		given(albumRepo.existsByIdPhotoId(pid)).willReturn(false);
		willDoNothing().given(photoRepo).deleteById(pid);

		// when
		service.deletePhoto(pid, memberId);

		// then
		then(albumRepo).should().deleteByIdPhotoIdAndIdMemberId(pid, memberId);
		then(photoRepo).should().deleteById(pid);
	}
}

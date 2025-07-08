package com.picpic.server.photo.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.service.usecase.PhotoUseCase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/albums")
public class PhotoController {

	private final PhotoUseCase photoUseCase;

	/**
	 * 무한 스크롤용 커서 기반 리스트 조회
	 * @param search  검색어 (옵션)
	 * @param cursor  ISO-8601 포맷 createdAt 커서 (옵션, 없으면 최신부터)
	 * @param size    한 번에 가져올 개수 (기본 20)
	 */
	@GetMapping
	public List<PhotoListResponse> listAlbums(
		@RequestParam(required = false) String search,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "20") int size) {

		Instant createdAtCursor = (cursor != null)
			? Instant.parse(cursor)
			: Instant.now();  // 커서 없으면 현재 시점으로 시작

		return photoUseCase.listPhotos(search, createdAtCursor, size);
	}

	@GetMapping("/{albumId}")
	public PhotoDetailResponse getAlbum(
		@PathVariable String albumId,
		@AuthenticationPrincipal MemberPrincipalDetail member) {
		return photoUseCase.getPhotoDetail(albumId, member.memberId());
	}

	@DeleteMapping("/{albumId}")
	public void deleteAlbum(
		@PathVariable String albumId,
		@AuthenticationPrincipal MemberPrincipalDetail member) {
		photoUseCase.deletePhoto(albumId, member.memberId());
	}
}

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
		@AuthenticationPrincipal MemberPrincipalDetail member,
		@RequestParam(required = false) String search,
		// @RequestParam(defaultValue = "createdAt") String sortField,
		// @RequestParam(defaultValue = "desc") String sortOrder,
		@RequestParam(required = false) String cursor,
		@RequestParam(defaultValue = "20") int size) {

		Instant createdAtCursor = (cursor != null)
			? Instant.parse(cursor)
			: Instant.now();

		return photoUseCase.listPhotos(member.memberId(), search, createdAtCursor, size);

		// return photoUseCase.listPhotos(
		// 	member.memberId(),
		// 	search,
		// 	sortField,
		// 	sortOrder,
		// 	createdAtCursor,
		// 	size);
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

package com.picpic.server.photo.controller;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.service.usecase.PhotoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/albums")
public class PhotoController {

    private final PhotoUseCase photoUseCase;

    @GetMapping
    public Page<PhotoListResponse> listAlbums(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "asc") String sort,
            Pageable pageable) {
        return photoUseCase.listPhotos(search, sort, pageable);
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

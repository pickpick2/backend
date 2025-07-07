package com.picpic.server.photo.service.usecase;

import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PhotoUseCase {
    Page<PhotoListResponse> listPhotos(String search, String sort, Pageable pageable);
    PhotoDetailResponse getPhotoDetail(String photoId, long memberId);
    void deletePhoto(String photoId, long memberId);
}

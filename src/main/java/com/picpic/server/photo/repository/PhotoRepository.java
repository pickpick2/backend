package com.picpic.server.photo.repository;

import com.picpic.server.photo.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoEntity, String> {
    void deleteByPhotoId(String photoId);
}

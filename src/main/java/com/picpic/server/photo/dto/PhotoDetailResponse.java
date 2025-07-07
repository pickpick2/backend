package com.picpic.server.photo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PhotoDetailResponse {
    private String photoId;
    private String title;
    private String url;
    private Long createdAt;
    private List<Long> participantIds;
}

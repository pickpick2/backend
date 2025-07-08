package com.picpic.server.photo.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoDetailResponse {
	private String photoId;
	private String title;
	private String url;
	private Long createdAt;
	private List<Long> participantIds;
}

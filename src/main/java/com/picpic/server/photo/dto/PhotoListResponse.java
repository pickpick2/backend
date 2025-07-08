package com.picpic.server.photo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoListResponse {
	private String photoId;
	private String title;
	private String url;
	private Long createdAt;
}

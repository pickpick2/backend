package com.picpic.server.frame.dto;

public record FrameOptionResponse(
	Long frameId,
	String name,
	Integer slotCount,
	String frameImageUrl
) {
}

package com.picpic.server.frame.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpic.server.frame.dto.FrameOptionResponse;
import com.picpic.server.frame.service.FrameOptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/room/{roomId}/frames")
@RequiredArgsConstructor
public class FrameOptionController {
	private final FrameOptionService frameOptionService;

	@GetMapping
	public ResponseEntity<List<FrameOptionResponse>> getFrameOptions(
		@PathVariable Long roomId
	) {
		List<FrameOptionResponse> options = frameOptionService.getFrameOptions(roomId);
		return ResponseEntity.ok(options);
	}
}

package com.picpic.server.frame.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.frame.dto.FrameOptionResponse;
import com.picpic.server.frame.repository.FrameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameOptionService {

	private final FrameRepository frameRepository;

	@Transactional(readOnly = true)
	public List<FrameOptionResponse> getAllFrameOptions() {
		return frameRepository.findByDeletedAtIsNull().stream()
			.map(frame -> new FrameOptionResponse(
				frame.getFrameId(),
				frame.getName(),
				frame.getSlotCount(),
				frame.getFrameImageUrl()
			))
			.toList();
	}
}

package com.picpic.server.frame.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.frame.dto.FrameOptionResponse;
import com.picpic.server.frame.repository.FrameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameOptionService {

	private final FrameRepository frameRepository;
	private final RedisTemplate<String, Object> redisTemplate;

	@Transactional(readOnly = true)
	public List<FrameOptionResponse> getFrameOptions(Long roomId) {
		// 1) Redis에서 참가자 ID 셋을 읽어와 인원 수 계산
		String participantKey = "room:" + roomId + ":participants";
		Set<Object> members = redisTemplate.opsForSet().members(participantKey);
		int participantCount = (members != null) ? members.size() : 0;

		// 2) JPA로 삭제되지 않은 프레임 전체 조회 → slotCount 기준으로 필터링
		return frameRepository.findByDeletedAtIsNull().stream()
			.filter(frame -> frame.getSlotCount() == participantCount)
			.map(frame -> new FrameOptionResponse(
				frame.getFrameId(),
				frame.getName(),
				frame.getSlotCount(),
				frame.getFrameImageUrl()
			))
			.toList();
	}
}

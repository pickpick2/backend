package com.picpic.server.frame.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameVoteService {

	private final RedisTemplate<String, Object> redisTemplate;

	public void vote(String roomId, Long frameId, Long memberId) {
		String key = voteKey(roomId, frameId);
		redisTemplate.opsForSet().add(key, memberId.toString());
	}

	public Set<Long> getVoters(String roomId, Long frameId) {
		String key = voteKey(roomId, frameId);
		Set<Object> raw = redisTemplate.opsForSet().members(key);
		if (raw == null)
			return Set.of();
		return raw.stream()
			.map(o -> Long.parseLong(o.toString()))
			.collect(Collectors.toSet());
	}

	public Map<Long, Integer> getVoteCounts(String roomId, List<Long> candidateFrameIds) {
		Map<Long, Integer> result = new HashMap<>();
		for (Long frameId : candidateFrameIds) {
			String key = voteKey(roomId, frameId);
			int count = redisTemplate.opsForSet().size(key).intValue();
			result.put(frameId, count);
		}
		return result;
	}

	public Long getMostVotedFrameId(String roomId, List<Long> candidateFrameIds) {
		Map<Long, Integer> voteCounts = getVoteCounts(roomId, candidateFrameIds);
		int max = voteCounts.values().stream().mapToInt(v -> v).max().orElse(0);
		List<Long> topFrames = voteCounts.entrySet().stream()
			.filter(entry -> entry.getValue() == max)
			.map(Map.Entry::getKey)
			.toList();
		Collections.shuffle(topFrames); // 동점 랜덤 처리
		return topFrames.get(0);
	}

	private String voteKey(String roomId, Long frameId) {
		return "room:" + roomId + ":frame:" + frameId + ":votes";
	}
}

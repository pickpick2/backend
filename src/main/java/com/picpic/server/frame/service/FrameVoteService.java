package com.picpic.server.frame.service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.picpic.server.frame.dto.ws.FrameActionResponse;
import com.picpic.server.frame.dto.ws.FrameMessageType;
import com.picpic.server.frame.dto.ws.FrameWsMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameVoteService {

	private final SimpMessagingTemplate template;
	private final RedisTemplate<String, Object> redisTemplate;

	private final ScheduledExecutorService scheduler =
			Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	private final Map<String, ScheduledFuture<?>> voteTasks = new ConcurrentHashMap<>();
	private final Map<String, Set<Long>> voteCompletions = new ConcurrentHashMap<>();
	private final Map<String, Integer> participantCounts = new ConcurrentHashMap<>();
	private final Map<String, Long> selectedOptions = new ConcurrentHashMap<>();

	public void startVoting(String roomId, List<Long> participantIds) {
		redisTemplate.delete(voteKey(roomId));
		for (Long uid : participantIds) {
			redisTemplate.delete(userVoteKey(roomId, uid));
		}
		participantCounts.put(roomId, participantIds.size());
		voteCompletions.put(roomId, ConcurrentHashMap.newKeySet());

		long now = System.currentTimeMillis();
		FrameActionResponse payload = FrameActionResponse.builder()
				.type(FrameMessageType.START_VOTE)
				.startTimeMillis(now)
				.durationSeconds(20)
				.build();

		template.convertAndSend("/topic/room/" + roomId,
				new FrameWsMessage<>(FrameMessageType.START_VOTE, null, payload));

		ScheduledFuture<?> future = scheduler.schedule(
				() -> finalizeVote(roomId), 20, TimeUnit.SECONDS);
		voteTasks.put(roomId, future);
	}

	public void submitVote(String roomId, Long userId, Long optionId) {
		// 이전 선택 조회
		String userKey = userVoteKey(roomId, userId);
		Object prevObj = redisTemplate.opsForValue().get(userKey);
		Long prev = (prevObj == null) ? null : toLong(prevObj);

		// 이전 표 -1
		if (prev != null && !prev.equals(optionId)) {
			redisTemplate.opsForHash().increment(voteKey(roomId), prev.toString(), -1);
		}
		// 새 표 +1
		redisTemplate.opsForHash().increment(voteKey(roomId), optionId.toString(), 1);
		// 현재 선택 저장
		redisTemplate.opsForValue().set(userKey, optionId);

		// 최소 한 번이라도 선택했으면 완료로 간주(바꾸는 건 허용)
		voteCompletions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);

		Integer total = participantCounts.get(roomId);
		if (total != null && voteCompletions.get(roomId).size() == total) {
			ScheduledFuture<?> task = voteTasks.remove(roomId);
			if (task != null) task.cancel(false);
			finalizeVote(roomId);
		}
	}

	private synchronized void finalizeVote(String roomId) {
		if (!participantCounts.containsKey(roomId)) return;

		Map<Object, Object> raw = redisTemplate.opsForHash().entries(voteKey(roomId));
		Long selectedOption;
		if (raw == null || raw.isEmpty()) {
			selectedOption = 0L; // 기본값(원하면 바꿔)
		} else {
			Map<Long, Integer> counts = raw.entrySet().stream().collect(Collectors.toMap(
					e -> toLong(e.getKey()),
					e -> toInt(e.getValue()),
					Integer::sum));

			int maxVotes = counts.values().stream().max(Integer::compareTo).orElse(0);
			List<Long> topOptions = counts.entrySet().stream()
					.filter(e -> e.getValue() == maxVotes)
					.map(Map.Entry::getKey)
					.toList();

			selectedOption = topOptions.get(new Random().nextInt(topOptions.size()));
		}

		selectedOptions.put(roomId, selectedOption);

		FrameActionResponse payload = FrameActionResponse.builder()
				.type(FrameMessageType.VOTE_FRAME)
				.selectedFrameId(selectedOption)
				.build();

		template.convertAndSend("/topic/room/" + roomId,
				new FrameWsMessage<>(FrameMessageType.VOTE_FRAME, null, payload));

		cleanup(roomId);
	}

	public Long getSelectedOption(String roomId) {
		return selectedOptions.get(roomId);
	}

	// ───────── helpers ─────────
	private String voteKey(String roomId) { return "room:" + roomId + ":votes"; }
	private String userVoteKey(String roomId, Long userId) { return "room:" + roomId + ":vote:user:" + userId; }

	private Long toLong(Object o) {
		if (o == null) return null;
		if (o instanceof Long l) return l;
		if (o instanceof Integer i) return i.longValue();
		if (o instanceof String s) return Long.parseLong(s);
		if (o instanceof byte[] b) return Long.parseLong(new String(b));
		throw new IllegalArgumentException("Unsupported key type: " + o.getClass());
	}
	private Integer toInt(Object o) {
		if (o == null) return 0;
		if (o instanceof Integer i) return i;
		if (o instanceof Long l) return Math.toIntExact(l);
		if (o instanceof Double d) return d.intValue();
		if (o instanceof String s) return Integer.parseInt(s);
		if (o instanceof byte[] b) return Integer.parseInt(new String(b));
		throw new IllegalArgumentException("Unsupported value type: " + o.getClass());
	}
	private void cleanup(String roomId) {
		voteTasks.remove(roomId);
		voteCompletions.remove(roomId);
		participantCounts.remove(roomId);
	}
}

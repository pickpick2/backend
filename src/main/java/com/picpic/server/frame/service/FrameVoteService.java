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

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.picpic.server.frame.dto.ws.FrameActionResponse;
import com.picpic.server.frame.dto.ws.FrameMessageType;
import com.picpic.server.frame.dto.ws.FrameWsMessage;

import lombok.RequiredArgsConstructor;

/**
 * 프레임 투표 단계: 20초 타이머, 조기 종료, 확정 브로드캐스트
 * (원본 FrameVoteService.java :contentReference[oaicite:2]{index=2} 확장)
 */
@Service
@RequiredArgsConstructor
public class FrameVoteService {

	private final SimpMessagingTemplate template;

	// 스케줄러 풀
	private final ScheduledExecutorService scheduler =
		Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	// roomId → 스케줄된 Future
	private final Map<String, ScheduledFuture<?>> voteTasks = new ConcurrentHashMap<>();

	// roomId → 투표 완료한 userId 집합
	private final Map<String, Set<Long>> voteCompletions = new ConcurrentHashMap<>();

	// roomId → 참가자 수
	private final Map<String, Integer> participantCounts = new ConcurrentHashMap<>();

	private final Map<String, Long> selectedOptions = new ConcurrentHashMap<>();

	/**
	 * 투표 단계 시작 → 클라이언트에 START_VOTE 전송
	 */
	public void startVoting(String roomId, List<Long> participantIds) {
		participantCounts.put(roomId, participantIds.size());
		voteCompletions.put(roomId, ConcurrentHashMap.newKeySet());

		long now = System.currentTimeMillis();
		// 클라이언트에 카운트다운 정보 포함
		FrameActionResponse payload = FrameActionResponse.builder()
			.type(FrameMessageType.START_VOTE)
			.startTimeMillis(now)
			.durationSeconds(20)
			.build();

		template.convertAndSend(
			"/topic/room/" + roomId,
			new FrameWsMessage<>(FrameMessageType.START_VOTE, null, payload)
		);

		// 20초 후 자동 확정 스케줄
		ScheduledFuture<?> future = scheduler.schedule(
			() -> finalizeVote(roomId),
			20, TimeUnit.SECONDS
		);
		voteTasks.put(roomId, future);
	}

	/**
	 * 사용자가 투표를 제출했을 때 호출
	 */
	public void submitVote(String roomId, Long userId, Long optionId) {
		// (기존 vote 저장 로직 호출)
		// 예: frameVoteService.vote(roomId, optionId, userId);

		// 완료 추적
		Set<Long> done = voteCompletions.get(roomId);
		done.add(userId);

		// 모두 완료 시 조기 확정
		if (done.size() == participantCounts.get(roomId)) {
			voteTasks.get(roomId).cancel(false);
			finalizeVote(roomId);
		}
	}

	/**
	 * 투표 확정 및 브로드캐스트
	 */
	private synchronized void finalizeVote(String roomId) {
		// 중복 실행 방지
		if (!participantCounts.containsKey(roomId))
			return;

		String voteKey = "room:" + roomId + ":votes";
		@SuppressWarnings("unchecked")
		Map<String, Long> raw = (Map<String, Long>)redisTemplate.opsForHash().entries(voteKey);

		Long selectedOption;
		if (raw.isEmpty()) {
			// 투표자가 없으면 기본 프레임 선택 (예: 첫 번째 프레임)
			selectedOption = 0L;
		} else {
			// String key(optionId) → Long count
			Map<Long, Integer> counts = raw.entrySet().stream()
				.collect(Collectors.toMap(
					e -> Long.valueOf(e.getKey()),
					e -> e.getValue().intValue()
				));

			// 최댓값 계산
			int maxVotes = counts.values().stream()
				.max(Integer::compareTo)
				.orElse(0);

			// 동점 처리용 리스트
			List<Long> topOptions = counts.entrySet().stream()
				.filter(e -> e.getValue() == maxVotes)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

			// 랜덤으로 하나 선택
			selectedOption = topOptions.get(new Random().nextInt(topOptions.size()));
		}

		// 결과 저장
		selectedOptions.put(roomId, selectedOption);

		// 확정 결과 브로드캐스트
		FrameActionResponse payload = FrameActionResponse.builder()
			.type(FrameMessageType.VOTE_FRAME)
			.selectedFrameId(selectedOption)
			.build();
		template.convertAndSend(
			"/topic/room/" + roomId,
			new FrameWsMessage<>(FrameMessageType.VOTE_FRAME, null, payload)
		);

		// 메모리 정리
		voteTasks.remove(roomId);
		voteCompletions.remove(roomId);
		participantCounts.remove(roomId);
	}

	public Long getSelectedOption(String roomId) {
		return selectedOptions.get(roomId);
	}

}

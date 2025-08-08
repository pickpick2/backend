package com.picpic.server.frame.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.frame.dto.ws.FrameActionResponse;
import com.picpic.server.frame.dto.ws.FrameMessageType;
import com.picpic.server.frame.dto.ws.FrameWsMessage;
import com.picpic.server.frame.entity.Frame;
import com.picpic.server.frame.repository.FrameRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameCellService {

	private final SimpMessagingTemplate template;
	private final RedisTemplate<String, Object> redisTemplate;
	private final FrameRepository frameRepository;

	private final ScheduledExecutorService scheduler =
		Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	private final Map<String, ScheduledFuture<?>> cellTasks = new ConcurrentHashMap<>();
	private final Map<String, Set<Long>> cellCompletions = new ConcurrentHashMap<>();
	private final Map<String, Integer> participantCounts = new ConcurrentHashMap<>();
	private final Map<String, List<Integer>> availableCells = new ConcurrentHashMap<>();

	/**
	 * 셀 선택 단계 시작: 20초 타이머와 START_CELL 메시지 브로드캐스트
	 */
	public void startCellSelection(String roomId, Long frameOption, List<Long> participantIds) {
		participantCounts.put(roomId, participantIds.size());
		cellCompletions.put(roomId, ConcurrentHashMap.newKeySet());

		// 프레임의 slotCount 만큼 인덱스 생성 (0부터 slotCount-1)
		List<Integer> cells = loadCellsForFrame(frameOption);
		availableCells.put(roomId, new ArrayList<>(cells));

		long now = System.currentTimeMillis();
		FrameActionResponse payload = FrameActionResponse.builder()
			.type(FrameMessageType.START_CELL)
			.startTimeMillis(now)
			.durationSeconds(20)
			.build();

		template.convertAndSend(
			"/topic/room/" + roomId,
			new FrameWsMessage<>(FrameMessageType.START_CELL, null, payload)
		);

		ScheduledFuture<?> future = scheduler.schedule(
			() -> finalizeCellSelection(roomId),
			20, TimeUnit.SECONDS
		);
		cellTasks.put(roomId, future);
	}

	/**
	 * 사용자가 셀을 선택했을 때
	 */
	@Transactional
	public void selectCell(String roomId, Long userId, int cellIndex) {
		String key = "room:" + roomId + ":cell:" + cellIndex + ":selections";
		redisTemplate.opsForSet().add(key, userId);

		// 중간 상태 업데이트
		FrameActionResponse mid = FrameActionResponse.builder()
			.type(FrameMessageType.SELECT)
			.build();
		template.convertAndSend(
			"/topic/room/" + roomId,
			new FrameWsMessage<>(FrameMessageType.SELECT, null, mid)
		);

		Set<Long> done = cellCompletions.get(roomId);
		done.add(userId);

		if (done.size() == participantCounts.get(roomId)) {
			cellTasks.get(roomId).cancel(false);
			finalizeCellSelection(roomId);
		}
	}

	/**
	 * 셀 배정 확정 및 브로드캐스트
	 */
	private synchronized void finalizeCellSelection(String roomId) {
		if (!participantCounts.containsKey(roomId))
			return;

		// 1) 자동 배정: 미선택 유저에게 랜덤 셀 할당
		List<Integer> cells = availableCells.get(roomId);
		Collections.shuffle(cells);

		List<Long> users = getParticipantIds(roomId);
		Set<Long> done = cellCompletions.get(roomId);
		Iterator<Integer> cellIt = cells.iterator();
		for (Long userId : users) {
			if (!done.contains(userId) && cellIt.hasNext()) {
				int idx = cellIt.next();
				String key = "room:" + roomId + ":cell:" + idx + ":selections";
				redisTemplate.opsForSet().add(key, userId);
			}
		}

		// 2) 최종 매핑 수집
		Map<Integer, Long> assignments = loadAllCellAssignments(roomId);

		FrameActionResponse payload = FrameActionResponse.builder()
			.type(FrameMessageType.CONFIRM_CELL)
			.cellAssignments(assignments)
			.build();

		template.convertAndSend(
			"/topic/room/" + roomId,
			new FrameWsMessage<>(FrameMessageType.CONFIRM_CELL, null, payload)
		);

		// 3) 클린업
		cellTasks.remove(roomId);
		cellCompletions.remove(roomId);
		participantCounts.remove(roomId);
		availableCells.remove(roomId);
	}

	/**
	 * Frame 엔티티에서 slotCount를 읽어 0..slotCount-1 리스트 생성
	 */
	private List<Integer> loadCellsForFrame(Long frameOption) {
		Frame frame = frameRepository.findById(frameOption)
			.orElseThrow(() -> new IllegalArgumentException("Invalid frameOption: " + frameOption));
		return IntStream.range(0, frame.getSlotCount())
			.boxed()
			.toList();
	}

	/**
	 * 방(roomId)에 참여한 userId 리스트 조회
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getParticipantIds(String roomId) {
		String key = "room:" + roomId + ":participants";
		Set<Object> members = redisTemplate.opsForSet().members(key);
		if (members == null) {
			return List.of();
		}
		return members.stream()
			.map(o -> (Long)o)
			.collect(Collectors.toList());
	}

	/**
	 * 각 셀 인덱스별로 Redis에 저장된 단일 선택자를 읽어 Map&lt;cellIndex, userId&gt; 형태로 반환
	 */
	private Map<Integer, Long> loadAllCellAssignments(String roomId) {
		Map<Integer, Long> result = new HashMap<>();
		for (Integer idx : availableCells.get(roomId)) {
			String key = "room:" + roomId + ":cell:" + idx + ":selections";
			@SuppressWarnings("unchecked")
			Set<Object> members = redisTemplate.opsForSet().members(key);
			if (members != null && !members.isEmpty()) {
				result.put(idx, (Long)members.iterator().next());
			}
		}
		return result;
	}
}

package com.picpic.server.frame.service;

import java.time.Duration;
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
	public void startCellSelection(String roomId, Long selectedFrameId, List<Long> participantIds) {
		// 참가자/완료 리셋
		participantCounts.put(roomId, participantIds.size());
		cellCompletions.put(roomId, ConcurrentHashMap.newKeySet());

		int slotCount = resolveSlotCount(selectedFrameId);
		for (int idx = 0; idx < slotCount; idx++) {
			redisTemplate.delete("room:" + roomId + ":cell:" + idx + ":owner");
		}
		for (Long uid : participantIds) {
			redisTemplate.delete("room:" + roomId + ":cell:ownerByUser:" + uid);
		}
		List<Integer> cells = IntStream.range(0, slotCount).boxed().collect(Collectors.toList());
		availableCells.put(roomId, cells);

		long now = System.currentTimeMillis();
		FrameActionResponse payload = FrameActionResponse.builder()
				.type(FrameMessageType.START_CELL)
				.startTimeMillis(now)
				.durationSeconds(20)
				.build();

		template.convertAndSend("/topic/room/" + roomId,
				new FrameWsMessage<>(FrameMessageType.START_CELL, null, payload));

		ScheduledFuture<?> future = scheduler.schedule(
				() -> finalizeCellSelection(roomId), 20, TimeUnit.SECONDS);
		cellTasks.put(roomId, future);
	}

	/**
	 * 사용자가 셀을 선택했을 때
	 */
	public void selectCell(String roomId, Long userId, int cellIndex) {
		String userOwnKey = "room:" + roomId + ":cell:ownerByUser:" + userId;
		String cellOwnerKey = "room:" + roomId + ":cell:" + cellIndex + ":owner";

		Object curOwnedObj = redisTemplate.opsForValue().get(userOwnKey);
		Integer curOwned = (curOwnedObj == null) ? null : toInt(curOwnedObj);

		// 같은 셀을 다시 터치 → 취소(해제)
		if (curOwned != null && curOwned == cellIndex) {
			Object owner = redisTemplate.opsForValue().get(cellOwnerKey);
			if (owner != null && toLong(owner).equals(userId)) {
				redisTemplate.delete(cellOwnerKey);
				redisTemplate.delete(userOwnKey);
				// 완료 취소
				cellCompletions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).remove(userId);
				broadcastSelectMid(roomId);
			}
			return;
		}

		// 새 셀 점유 시도 (이미 누군가 잡았으면 실패)
		Boolean locked = redisTemplate.opsForValue().setIfAbsent(cellOwnerKey, userId);
		if (locked == null || !locked) {
			// 점유 실패: 이미 다른 사람이 선점 → 그냥 무시하거나 에러 큐로 알림
			return;
		}

		// 이전 셀 해제
		if (curOwned != null) {
			String prevOwnerKey = "room:" + roomId + ":cell:" + curOwned + ":owner";
			Object prevOwner = redisTemplate.opsForValue().get(prevOwnerKey);
			if (prevOwner != null && toLong(prevOwner).equals(userId)) {
				redisTemplate.delete(prevOwnerKey);
			}
		}

		// 유저→셀 역매핑 갱신 + 완료 체크
		redisTemplate.opsForValue().set(userOwnKey, cellIndex);
		cellCompletions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
		broadcastSelectMid(roomId);

		// 모두 완료 → 조기 확정
		Integer total = participantCounts.get(roomId);
		if (total != null && cellCompletions.get(roomId).size() == total) {
			ScheduledFuture<?> task = cellTasks.remove(roomId);
			if (task != null) task.cancel(false);
			finalizeCellSelection(roomId);
		}
	}


	/**
	 * 셀 배정 확정 및 브로드캐스트
	 */
	private synchronized void finalizeCellSelection(String roomId) {
		if (!participantCounts.containsKey(roomId)) return;

		List<Integer> cells = new ArrayList<>(availableCells.getOrDefault(roomId, List.of()));
		Collections.shuffle(cells);

		List<Long> users = getParticipantIds(roomId);
		Set<Long> done = cellCompletions.get(roomId);
		Map<Integer, Long> assignments = new HashMap<>();

		// 1) 이미 점유된 셀 수집 + 미선택 유저 목록 도출
		for (Integer idx : new ArrayList<>(cells)) {
			String k = "room:" + roomId + ":cell:" + idx + ":owner";
			Object owner = redisTemplate.opsForValue().get(k);
			if (owner != null) {
				Long uid = toLong(owner);
				assignments.put(idx, uid);
			}
		}
		List<Long> unselectedUsers = users.stream().filter(u -> !done.contains(u)).toList();

		// 2) 남은 셀 자동 배정
		for (Integer idx : cells) {
			if (assignments.containsKey(idx)) continue;
			if (unselectedUsers.isEmpty()) break;

			Long uid = unselectedUsers.get(0);
			String cellOwnerKey = "room:" + roomId + ":cell:" + idx + ":owner";
			Boolean locked = redisTemplate.opsForValue().setIfAbsent(cellOwnerKey, uid);
			if (locked != null && locked) {
				assignments.put(idx, uid);
				// 역매핑/완료 갱신
				redisTemplate.opsForValue().set("room:" + roomId + ":cell:ownerByUser:" + uid, idx);
				cellCompletions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(uid);
				unselectedUsers = unselectedUsers.subList(1, unselectedUsers.size());
			}
		}

		// 3) 브로드캐스트
		FrameActionResponse payload = FrameActionResponse.builder()
				.type(FrameMessageType.CONFIRM_CELL)
				.cellAssignments(assignments)
				.build();

		template.convertAndSend("/topic/room/" + roomId,
				new FrameWsMessage<>(FrameMessageType.CONFIRM_CELL, null, payload));

		// 메모리 정리
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
				.map(this::toLong)
				.collect(Collectors.toList());
	}


	/** ✅ 확정된 프레임의 칸 수(slotCount) 조회 (캐시 → DB 순) */
	public int resolveSlotCount(Long frameId) {
		// 1) 캐시 먼저 조회
		String cacheKey = "frame:" + frameId + ":slotCount";
		Object cached = redisTemplate.opsForValue().get(cacheKey);
		if (cached != null) {
			return toInt(cached);
		}

		// 2) DB 조회
		Frame frame = frameRepository.findById(frameId)
				.orElseThrow(() -> new IllegalArgumentException("Frame not found: " + frameId));

		int slotCount = frame.getSlotCount();

		// 3) 캐시에 저장 (선택: TTL 지정)
		redisTemplate.opsForValue().set(cacheKey, slotCount, Duration.ofHours(6));
		return slotCount;
	}

	private void broadcastSelectMid(String roomId) {
		FrameActionResponse mid = FrameActionResponse.builder()
				.type(FrameMessageType.SELECT)
				.build();
		template.convertAndSend("/topic/room/" + roomId,
				new FrameWsMessage<>(FrameMessageType.SELECT, null, mid));
	}
	private Integer toInt(Object o) {
		if (o == null) return null;
		if (o instanceof Integer i) return i;
		if (o instanceof Long l) return Math.toIntExact(l);
		if (o instanceof String s) return Integer.parseInt(s);
		if (o instanceof byte[] b) return Integer.parseInt(new String(b));
		throw new IllegalArgumentException("Unsupported: " + o.getClass());
	}
	private Long toLong(Object o) {
		if (o == null) return null;
		if (o instanceof Long l) return l;
		if (o instanceof Integer i) return i.longValue();
		if (o instanceof String s) return Long.parseLong(s);
		if (o instanceof byte[] b) return Long.parseLong(new String(b));
		throw new IllegalArgumentException("Unsupported: " + o.getClass());
	}

}

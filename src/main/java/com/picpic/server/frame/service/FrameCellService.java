package com.picpic.server.frame.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameCellService {

	private final RedisTemplate<String, Object> redisTemplate;

	private String cellKey(String roomId, Long frameId) {
		return "frame:" + roomId + ":" + frameId + ":cells";
	}

	private String memberKey(String roomId, Long frameId) {
		return "frame:" + roomId + ":" + frameId + ":members";
	}

	public boolean selectCell(String roomId, Long frameId, int cellIndex, Long memberId) {
		HashOperations<String, String, String> ops = redisTemplate.opsForHash();

		String cellsKey = cellKey(roomId, frameId);
		String membersKey = memberKey(roomId, frameId);

		if (ops.hasKey(cellsKey, String.valueOf(cellIndex))) {
			return false;
		}

		ops.put(cellsKey, String.valueOf(cellIndex), String.valueOf(memberId));
		ops.put(membersKey, String.valueOf(memberId), String.valueOf(cellIndex));

		return true;
	}

	public int getSelectedCount(String roomId, Long frameId) {
		return redisTemplate.opsForHash().size(cellKey(roomId, frameId)).intValue();
	}

	public boolean isAllSelected(String roomId, Long frameId, int slotCount) {
		return getSelectedCount(roomId, frameId) >= slotCount;
	}

	public void assignRemaining(String roomId, Long frameId, List<Long> allMemberIds, int slotCount) {
		HashOperations<String, String, String> ops = redisTemplate.opsForHash();
		String cellsKey = cellKey(roomId, frameId);
		String membersKey = memberKey(roomId, frameId);

		Set<String> selectedMemberIds = ops.entries(membersKey).keySet();

		List<Long> remainingMembers = allMemberIds.stream()
			.filter(mid -> !selectedMemberIds.contains(String.valueOf(mid)))
			.collect(Collectors.toList());

		Set<String> usedCellIndexes = ops.entries(cellsKey).keySet();
		List<Integer> availableIndexes = new ArrayList<>();
		for (int i = 0; i < slotCount; i++) {
			if (!usedCellIndexes.contains(String.valueOf(i))) {
				availableIndexes.add(i);
			}
		}

		if (availableIndexes.size() < remainingMembers.size()) {
			throw new IllegalStateException("남은 셀보다 미선택 사용자가 더 많습니다.");
		}

		Collections.shuffle(availableIndexes);

		for (int i = 0; i < remainingMembers.size(); i++) {
			int cellIndex = availableIndexes.get(i);
			Long memberId = remainingMembers.get(i);

			ops.put(cellsKey, String.valueOf(cellIndex), String.valueOf(memberId));
			ops.put(membersKey, String.valueOf(memberId), String.valueOf(cellIndex));
		}
	}

	public Integer getSelectedCellIndex(String roomId, Long frameId, Long memberId) {
		Object value = redisTemplate.opsForHash().get(memberKey(roomId, frameId), String.valueOf(memberId));
		return value != null ? Integer.parseInt((String)value) : null;
	}

	public Map<Integer, Long> getAllSelections(String roomId, Long frameId) {
		Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(cellKey(roomId, frameId));
		Map<Integer, Long> result = new HashMap<>();
		for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
			result.put(Integer.parseInt((String)entry.getKey()), Long.parseLong((String)entry.getValue()));
		}
		return result;
	}
}

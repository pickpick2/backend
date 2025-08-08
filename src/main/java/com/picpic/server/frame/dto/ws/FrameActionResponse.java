package com.picpic.server.frame.dto.ws;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FrameActionResponse {
	// 메시지 타입 (FrameMessageType)
	private FrameMessageType type;

	// 중간 상태(투표자/셀 선택자) 전송용
	private List<UserProfile> profiles;

	// 카운트다운 시작 시각 (Epoch milli)
	private Long startTimeMillis;

	// 제한 시간 (초)
	private Integer durationSeconds;

	// 투표 확정 시: 선택된 프레임 ID
	private Long selectedFrameId;

	// 셀 확정 시: cellIndex → memberId 맵
	private Map<Integer, Long> cellAssignments;
}

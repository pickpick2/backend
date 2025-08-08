package com.picpic.server.frame.service.ws;

import java.util.List;

import org.springframework.stereotype.Service;

import com.picpic.server.frame.dto.ws.FrameMessageType;
import com.picpic.server.frame.service.FrameCellService;
import com.picpic.server.frame.service.FrameVoteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameActionService {

	private final FrameVoteService frameVoteService;
	private final FrameCellService frameCellService;

	/**
	 * 투표 단계 시작 로직 위임
	 */
	public void startVote(String roomId) {
		List<Long> participantIds = frameCellService.getParticipantIds(roomId);
		frameVoteService.startVoting(roomId, participantIds);
	}

	/**
	 * 셀 선택 단계 시작 로직 위임
	 */
	public void startCell(String roomId) {
		Long selectedFrame = frameVoteService.getSelectedOption(roomId);
		List<Long> participantIds = frameCellService.getParticipantIds(roomId);
		frameCellService.startCellSelection(roomId, selectedFrame, participantIds);
	}

	/**
	 * VOTE / SELECT 액션 위임
	 */
	public void handleAction(
		String roomId,
		Long userId,
		Long targetId,
		FrameMessageType type,
		String requestId
	) {
		if (type == FrameMessageType.VOTE) {
			frameVoteService.submitVote(roomId, userId, targetId);
		} else if (type == FrameMessageType.SELECT) {
			frameCellService.selectCell(roomId, userId, targetId.intValue());
		}
	}
}
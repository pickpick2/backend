package com.picpic.server.frame.dto.ws;

public enum FrameMessageType {
	// 투표 단계 시작
	START_VOTE,
	// 투표 결과 확정
	VOTE_FRAME,
	// 셀 선택 단계 시작
	START_CELL,
	// 셀 선택 결과 확정
	CONFIRM_CELL,
	//프레임 투표 중간 업데이트
	VOTE,
	//셀 선택 중간 업데이트
	SELECT
}

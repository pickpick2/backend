package com.picpic.server.frame.entity;

public enum FrameCellStatus {
	AVAILABLE,  // 아직 선택되지 않음
	SELECTED,   // 사용자가 직접 선택함
	ASSIGNED    // 서버에 의해 랜덤 배정됨
}

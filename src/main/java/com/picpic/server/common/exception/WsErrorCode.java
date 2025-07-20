package com.picpic.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WsErrorCode {

	NOT_FOUND_ROOM("WS1001", "방을 찾을 수 없습니다."),
	ALREADY_CONNECTED("WS1002", "이미 연결된 방이 존재합니다."),
	EXCEED_ROOM_CAPACITY("WS1003", "방의 인원을 초과하였습니다."),
	MISSING_MEMBER_INFO("WS1004", "회원 정보가 누락되었습니다."),
	USER_NOT_IN_ROOM("WS1005", "방에 없는 회원입니다."),
	ACCESS_DENIED_CREATOR_REQUIRED("WS1006", "방장 권한이 없습니다."),
	NOT_ALL_READY("WS1007", "아직 준비되지 않은 멤버가 있습니다."),
	UNAVAILABLE_TOKEN("WS1008", "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN("WS1009", "토큰이 만료됐습니다.");

	private final String code;
	private final String message;

}

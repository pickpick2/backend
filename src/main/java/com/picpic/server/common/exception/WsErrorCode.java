package com.picpic.server.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WsErrorCode {

	NOT_FOUND_ROOM("1001", "방을 찾을 수 없습니다."),
	ALREADY_CONNECTED("1002", "이미 연결된 방이 존재합니다."),
	EXCEED_ROOM_CAPACITY("1003", "방의 인원을 초과하였습니다."),
	MISSING_MEMBER_INFO("1004", "회원 정보가 누락되었습니다."),
	USER_NOT_IN_ROOM("1005", "방에 없는 회원입니다."),
	ACCESS_DENIED_CREATOR_REQUIRED("1006", "방장 권한이 없습니다.");


	private final String code;
	private final String message;

}

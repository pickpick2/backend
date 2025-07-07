package com.picpic.server.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	// Unauthorized
	UNAUTHORIZED("1001", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
	FORBIDDEN("1002", HttpStatus.FORBIDDEN, "권한이 없습니다."),
	EXPIRED_TOKEN("1003", HttpStatus.UNAUTHORIZED, "토큰이 만료됐습니다."),
	UNAVAILABLE_TOKEN("1004", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	FORBIDDEN_ACCESS("1005", HttpStatus.FORBIDDEN, "사진 업로드 권한이 없습니다."),

	// Not Found
	NOT_FOUND_MEMBER("3001", HttpStatus.NOT_FOUND, "요청하신 유저를 찾을 수 없습니다."),
	NOT_FOUND_SESSION("3004", HttpStatus.NOT_FOUND, "요청하신 세션을 찾을 수 없습니다."),

	// Bad Request
	BAD_REQUEST("4000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_INPUT_VALUE("4001", HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
	NO_STICKER("4002", HttpStatus.BAD_REQUEST, "요청하신 스티커가 없습니다."),
	NOT_PARTICIPANT("4003", HttpStatus.BAD_REQUEST, "참여자가 아닙니다."),

	// Internal Error
	INTERNAL_ERROR("5000", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류입니다.");

	private final String code;
	private final HttpStatus status;
	private final String message;

	ErrorCode(String code, HttpStatus status, String message) {
		this.code = code;
		this.status = status;
		this.message = message;
	}
}
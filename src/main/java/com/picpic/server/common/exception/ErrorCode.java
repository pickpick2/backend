package com.picpic.server.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {


    NOT_FOUND_MEMBER("3001", HttpStatus.NOT_FOUND, "요청하신 유저를 찾을 수 없습니다."),
    NOT_FOUND_SESSION("3004", HttpStatus.NOT_FOUND, "요청하신 세션을 찾을 수 없습니다."),

    NO_STICKER("4001",HttpStatus.BAD_REQUEST, "요청하신 스티커가 없습니다."),
    NOT_PARTICIPANT("4002", HttpStatus.BAD_REQUEST, "참여자가 아닙니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
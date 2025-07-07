package com.picpic.server.common.response;

import com.picpic.server.common.exception.ErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

	private int code;
	private String message;
	private T data;

	public static <T> ApiResponse<T> success() {
		return ApiResponse.<T>builder()
			.code(200)
			.message("success")
			.data(null)
			.build();
	}

	public static <T> ApiResponse<T> success(T data) {
		return ApiResponse.<T>builder()
			.code(200)
			.message("success")
			.data(null)
			.build();
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode) {
		return ApiResponse.<T>builder()
			.code(Integer.parseInt(errorCode.getCode()))
			.message(errorCode.getMessage())
			.data(null)
			.build();
	}

	public static <T> ApiResponse<T> error(ErrorCode errorCode, T data) {
		return ApiResponse.<T>builder()
			.code(Integer.parseInt(errorCode.getCode()))
			.message(errorCode.getMessage())
			.data(data)
			.build();
	}
}

package com.picpic.server.common.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiResponse<T> {

    private T data;
    private Error error;

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .data(null)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> error(Error error) {
        return ApiResponse.<T>builder()
                .data(null)
                .error(error)
                .build();
    }

    @Getter
    @Builder
    private  static class Error {
        private String message;
    }
}

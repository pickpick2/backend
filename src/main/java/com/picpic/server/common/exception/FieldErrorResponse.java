package com.picpic.server.common.exception;

import lombok.Builder;

@Builder
public record FieldErrorResponse(
	String field,
	Object rejectedValue,
	String reason
) {
}
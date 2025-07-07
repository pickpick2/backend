package com.picpic.server.common.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.picpic.server.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	protected ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
		ErrorCode code = e.getErrorCode();
		log.warn(e.getMessage(), e);
		return ResponseEntity
			.status(code.getStatus())
			.body(ApiResponse.error(code));
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String paramName = ex.getParameterName();
		String message = String.format("파라미터 '%s'는 필수입니다.", paramName);

		log.warn("Missing request parameter: {}", paramName);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(ApiResponse.error(
				ErrorCode.BAD_REQUEST
			));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
		HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
			.map(error -> FieldErrorResponse.builder()
				.field(error.getField())
				.rejectedValue(error.getRejectedValue())
				.reason(error.getDefaultMessage())
				.build())
			.toList();

		log.warn(ErrorCode.INVALID_INPUT_VALUE.getMessage(), errors);

		ApiResponse<List<FieldErrorResponse>> response = ApiResponse.error(ErrorCode.INVALID_INPUT_VALUE, errors);

		return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()).body(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ApiResponse<Void>> handleUnhandled(Exception e) {
		log.error(e.getMessage(), e);
		return ResponseEntity
			.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error(ErrorCode.INTERNAL_ERROR));
	}
}
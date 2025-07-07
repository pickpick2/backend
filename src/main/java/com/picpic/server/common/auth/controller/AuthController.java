package com.picpic.server.common.auth.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpic.server.common.auth.dto.GuestLoginResponseDTO;
import com.picpic.server.common.auth.dto.GuestLoginResultDTO;
import com.picpic.server.common.auth.service.AuthService;
import com.picpic.server.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/api/v1/guest")
	public ResponseEntity<ApiResponse<GuestLoginResponseDTO>> guestLogin(HttpServletResponse response) {
		GuestLoginResultDTO res = authService.guestLogin();

		ResponseCookie cookie = ResponseCookie.from("access-token", res.accessToken())
			.httpOnly(true)
			.secure(false)
			.path("/")
			.sameSite("None")
			.maxAge(60 * 60 * 24)
			.build();

		response.addHeader("Set-Cookie", cookie.toString());

		return ResponseEntity.ok(ApiResponse.success(GuestLoginResponseDTO.builder().memberId(res.memberId()).build()));
	}
}

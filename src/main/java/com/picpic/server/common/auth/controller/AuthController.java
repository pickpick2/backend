package com.picpic.server.common.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.picpic.server.common.auth.dto.GuestLoginResponseDTO;
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
		return ResponseEntity.ok(ApiResponse.success(authService.guestLogin()));
	}

}

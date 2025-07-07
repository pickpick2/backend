package com.picpic.server.common.auth;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String token = jwtTokenProvider.resolveToken(request);

		if (token == null) {
			//TODO: 공용 응답 처리
			return;
		}

		jwtTokenProvider.validateToken(token);
		Long memberId = jwtTokenProvider.getMemberId(token);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			memberId,
			null,
			null
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return
			path.startsWith("/h2") ||
				path.startsWith("/api/v1/sign-in") ||
				path.startsWith("/api/v1/sign-up") ||
				path.startsWith("/api/v1/guest");
	}

}

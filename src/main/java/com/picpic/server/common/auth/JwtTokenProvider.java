package com.picpic.server.common.auth;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	public String generateToken(Long memberId) {
		Date now = new Date();
		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expiration))
			.signWith(getSignInKey())
			.compact();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
			return true;
		} catch (ExpiredJwtException e) {
			// TODO: 공용 에러 처리
			e.printStackTrace();

		} catch (JwtException e) {
			// TODO: 공용 에러 처리
			e.printStackTrace();
		}
		return false;
	}

	public Long getMemberId(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(getSignInKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private SecretKey getSignInKey() {
		byte[] bytes = Base64.getDecoder()
			.decode(secret.getBytes(StandardCharsets.UTF_8));
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	public String resolveToken(HttpServletRequest request) {
		if (request.getCookies() == null)
			return null;

		return Arrays.stream(request.getCookies())
			.filter(cookie -> "access-token".equals(cookie.getName()))
			.findFirst()
			.map(Cookie::getValue)
			.orElse(null);
	}
}

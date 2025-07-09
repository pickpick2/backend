package com.picpic.server.common.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.picpic.server.common.exception.ApiException;
import com.picpic.server.common.exception.ErrorCode;
import com.picpic.server.member.entity.Member;
import com.picpic.server.member.repository.MemberRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final MemberRepository memberRepository;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	public String generateToken(Long memberId) {
		Member member = memberRepository.findById(memberId).orElseThrow(
			() -> new ApiException(ErrorCode.NOT_FOUND_MEMBER)
		);

		String nickname = member.getNickname();
		String role = member.getRole().toString();

		Date now = new Date();
		return Jwts.builder()
			.subject(String.valueOf(memberId))
			.claim("nickname", nickname)
			.claim("role", role)
			.issuedAt(now)
			.expiration(new Date(now.getTime() + expiration))
			.signWith(getSignInKey())
			.compact();
	}

	public void validateToken(String token) {
		try {
			Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException e) {
			throw new ApiException(ErrorCode.EXPIRED_TOKEN);

		} catch (JwtException e) {
			throw new ApiException(ErrorCode.UNAVAILABLE_TOKEN);
		}
	}

	public Long getMemberId(String token) {
		Claims claims = parseClaims(token);
		return Long.parseLong(claims.getSubject());
	}

	public String getNickname(String token) {
		Claims claims = parseClaims(token);
		return claims.get("nickname", String.class);
	}

	public Member.Role getRole(String token) {
		Claims claims = parseClaims(token);
		String roleStr = claims.get("role", String.class);
		return Member.Role.valueOf(roleStr);
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
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return null;
		}

		return authorizationHeader.substring(7);
	}
}

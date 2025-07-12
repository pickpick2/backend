package com.picpic.server.common.auth;

import java.security.Principal;

import com.picpic.server.member.entity.Member;

import lombok.Builder;

@Builder
public record MemberPrincipalDetail(
	Long memberId,
	String nickName,
	Member.Role role
) implements Principal {

	@Override
	public String getName() {
		return memberId.toString();
	}
}

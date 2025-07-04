package com.picpic.server.common.security;

import lombok.Builder;

import java.security.Principal;

@Builder
public record MemberPrincipalDetail (
        Long memberId,
        String nickName
) implements Principal{

    @Override
    public String getName() {
        return memberId.toString();
    }
}

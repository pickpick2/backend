package com.picpic.server.common.security;

import lombok.Builder;

@Builder
public record MemberPrincipalDetail (
        Long memberId
) {
}

package com.picpic.server.common.auth.dto;

import lombok.Builder;

@Builder
public record GuestLoginResponseDTO(Long memberId) {
}

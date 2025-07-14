package com.picpic.server.room.dto;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.enums.RoomMemberStatus;

import lombok.Builder;

@Builder
public record RoomMemberResponseDto(
        String memberName,
        RoomMemberStatus memberStatus
) {

    public static RoomMemberResponseDto from(RoomMember roomMember) {
        return RoomMemberResponseDto.builder()
                .memberName(roomMember.getMemberName())
                .memberStatus(roomMember.getMemberStatus())
                .build();
    }
}

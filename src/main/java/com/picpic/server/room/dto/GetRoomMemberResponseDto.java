package com.picpic.server.room.dto;

import com.picpic.server.room.domain.RoomMember;
import lombok.Builder;

import java.util.List;

@Builder
public record GetRoomMemberResponseDto(
        List<RoomMemberDto> members
) {
    public static GetRoomMemberResponseDto from(List<RoomMember> roomMembers) {

        List<RoomMemberDto> members = roomMembers.stream().map(RoomMemberDto::from).toList();

        return GetRoomMemberResponseDto.builder()
                .members(members)
                .build();
    }
}

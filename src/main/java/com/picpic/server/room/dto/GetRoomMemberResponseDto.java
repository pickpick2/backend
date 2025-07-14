package com.picpic.server.room.dto;

import com.picpic.server.room.domain.RoomMember;
import lombok.Builder;

import java.util.List;

@Builder
public record GetRoomMemberResponseDto(
        List<RoomMemberResponseDto> members
) {
    public static GetRoomMemberResponseDto from(List<RoomMember> roomMembers) {

        List<RoomMemberResponseDto> members = roomMembers.stream().map(RoomMemberResponseDto::from).toList();

        return GetRoomMemberResponseDto.builder()
                .members(members)
                .build();
    }
}

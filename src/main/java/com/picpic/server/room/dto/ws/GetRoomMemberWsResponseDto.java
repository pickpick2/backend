package com.picpic.server.room.dto.ws;

import java.util.List;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.RoomMemberResponseDto;

import lombok.Builder;

@Builder
public record GetRoomMemberWsResponseDto (
        List<RoomMemberResponseDto> members
) {
   public static GetRoomMemberWsResponseDto from(List<RoomMember> members) {
	   List<RoomMemberResponseDto> list = members.stream()
		   .map(RoomMemberResponseDto::from)
		   .toList();

	   return GetRoomMemberWsResponseDto.builder().members(list).build();
   }
}

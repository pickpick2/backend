package com.picpic.server.room.dto.ws;

import java.util.List;

import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.dto.RoomMemberDto;

import lombok.Builder;

@Builder
public record GetRoomMemberWsResponseDto (
        List<RoomMemberDto> members
) {
   public static GetRoomMemberWsResponseDto from(List<RoomMember> members) {
	   List<RoomMemberDto> list = members.stream()
		   .map(RoomMemberDto::from)
		   .toList();

	   return GetRoomMemberWsResponseDto.builder().members(list).build();
   }
}

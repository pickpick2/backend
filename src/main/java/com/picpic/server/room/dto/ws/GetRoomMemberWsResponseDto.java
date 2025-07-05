package com.picpic.server.room.dto.ws;

import com.picpic.server.room.dto.RoomMemberDto;
import lombok.Builder;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Builder
public record GetRoomMemberWsResponseDto (
        List<RoomMemberDto> members
) {
//    public static GetRoomMemberWsResponseDto from(Set<Object> members) {
//        members.stream().map(member ->
//                RoomMemberDto.builder().build();
//            member instanceof RoomMemberDto ? RoomMemberDto.builder().memberId(member).build(): null
//        ).toList();
//
//
//        return GetRoomMemberWsResponseDto.builder().members(m).build();
//    }
}

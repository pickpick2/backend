package com.picpic.server.room.dto;

import com.picpic.server.room.domain.RoomMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record RoomMemberDto (
        String memberName,
        RoomMemberStatus memberStatus
) {
    public enum RoomMemberStatus {
        CONNECTED, DISCONNECTED
    }

    public static RoomMemberDto from(RoomMember roomMember) {
        return RoomMemberDto.builder()
                .memberName(roomMember.getMemberName())
                .build();
    }
}

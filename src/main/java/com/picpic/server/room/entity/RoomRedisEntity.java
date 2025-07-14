package com.picpic.server.room.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@RedisHash(value = "room", timeToLive = 3600)
@NoArgsConstructor
@AllArgsConstructor
public class RoomRedisEntity {

    @Id
    private String roomId;

    private RoomMember creator;

    @Builder.Default
    private List<RoomMember> members = new ArrayList<>();

    @Builder.Default
    private int roomCapacity = 6;

    public RoomRedisEntity addMember(RoomMember newMember) {

        List<RoomMember> updatedMembers = new ArrayList<>();
        if(this.members != null) {
            updatedMembers = new ArrayList<>(this.members);
        }

        boolean alreadyExists = updatedMembers.stream()
                .anyMatch(member -> member.getMemberId().equals(newMember.getMemberId()));
        if (alreadyExists) {
            throw new WsException(WsErrorCode.ALREADY_CONNECTED);
        }

        updatedMembers.add(newMember);

        return this.toBuilder()
                .members(updatedMembers)
                .build();
    }

    public RoomRedisEntity subtractMember(Long memberId) {
        if (this.members == null) {
            throw new WsException(WsErrorCode.USER_NOT_IN_ROOM);
        }

        List<RoomMember> updatedMembers = this.members.stream()
                .filter(member -> !member.getMemberId().equals(memberId))
                .toList();

        return this.toBuilder()
                .members(updatedMembers)
                .build();
    }
}

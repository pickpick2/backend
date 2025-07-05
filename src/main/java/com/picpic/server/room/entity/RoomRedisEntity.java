package com.picpic.server.room.entity;

import com.picpic.server.room.domain.RoomMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

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

    public RoomRedisEntity addMember(RoomMember newMember) {

        List<RoomMember> updatedMembers = new ArrayList<>();
        if(this.members != null) {
            updatedMembers = new ArrayList<>(this.members);
        }

        boolean alreadyExists = updatedMembers.stream()
                .anyMatch(member -> member.getMemberId().equals(newMember.getMemberId()));
        if (alreadyExists) {
            throw new RuntimeException("User is already in the room. :" + newMember.getMemberId());
        }

        updatedMembers.add(newMember);

        return this.toBuilder()
                .members(updatedMembers)
                .build();
    }

    public RoomRedisEntity subtractMember(Long memberId) {
        if (this.members == null) {
            throw new RuntimeException("User does not exist in the room. :" + memberId);
        }

        List<RoomMember> updatedMembers = this.members.stream()
                .filter(member -> !member.getMemberId().equals(memberId))
                .toList();

        return this.toBuilder()
                .members(updatedMembers)
                .build();
    }
}

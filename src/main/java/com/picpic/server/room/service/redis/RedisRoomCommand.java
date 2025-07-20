package com.picpic.server.room.service.redis;

import java.util.List;

import org.springframework.stereotype.Component;

import com.picpic.server.common.auth.MemberPrincipalDetail;

import com.picpic.server.common.exception.WsErrorCode;
import com.picpic.server.common.exception.WsException;
import com.picpic.server.room.domain.RoomMember;
import com.picpic.server.room.entity.RoomRedisEntity;
import com.picpic.server.room.enums.RoomMemberStatus;
import com.picpic.server.room.repository.RoomRedisRepository;
import com.picpic.server.room.service.usecase.RedisRoomCommandUseCase;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisRoomCommand implements RedisRoomCommandUseCase {

	private final RoomRedisRepository roomRedisRepository;

	@Override
	public void create(String roomId, MemberPrincipalDetail memberPrincipal, Integer roomCapacity) {

		RoomRedisEntity room = RoomRedisEntity.builder()
			.roomCapacity(roomCapacity)
			.roomId(roomId)
			.creator(RoomMember.from(memberPrincipal))
			.build();

		roomRedisRepository.save(room);
	}

	@Override
	public void delete(String roomId) {
		roomRedisRepository.deleteById(roomId);
	}

    @Override
    public void addMember(String roomId, MemberPrincipalDetail memberPrincipalDetail) {
        RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
                .orElseThrow(() -> new WsException(WsErrorCode.NOT_FOUND_ROOM));

		RoomMember newMember = RoomMember.from(memberPrincipalDetail);
		RoomRedisEntity updatedEntity = roomEntity.addMember(newMember);

		roomRedisRepository.save(updatedEntity);
	}

    @Override
    public void subtractMember(String roomId, Long memberId) {
        RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
                .orElseThrow(() -> new WsException(WsErrorCode.NOT_FOUND_ROOM));

		RoomRedisEntity updatedEntity = roomEntity.subtractMember(memberId);

		roomRedisRepository.save(updatedEntity);
	}

    @Override
    public void updateRoomCapacity(String roomId, Integer roomCapacity) {
        RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
                .orElseThrow(() -> new WsException(WsErrorCode.NOT_FOUND_ROOM));

		RoomRedisEntity updatedRoom = roomEntity.toBuilder()
			.roomCapacity(roomCapacity)
			.build();

		roomRedisRepository.save(updatedRoom);
	}

	@Override
	public RoomMember updateReadyState(String roomId, Long memberId, RoomMemberStatus roomMemberStatus) {

		RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
			.orElseThrow(() -> new WsException(WsErrorCode.NOT_FOUND_ROOM));

		List<RoomMember> updatedMembers = roomEntity.getMembers().stream().map(member -> {
			if (member.getMemberId().equals(memberId)) {
				member.setMemberStatus(roomMemberStatus);
			}
			return member;
		}).toList();

		RoomRedisEntity updatedRoomEntity = roomEntity.toBuilder()
			.members(updatedMembers)
			.build();

		RoomRedisEntity save = roomRedisRepository.save(updatedRoomEntity);

		RoomMember roomMember = save.getMembers().stream()
			.filter(member -> member.getMemberId().equals(memberId))
			.findFirst()
			.get();

		return roomMember;
	}

	@Override
	public int updateBackground(String roomId, Integer backgroundId) {

		RoomRedisEntity roomEntity = roomRedisRepository.findById(roomId)
			.orElseThrow(() -> new WsException(WsErrorCode.NOT_FOUND_ROOM));

		RoomRedisEntity updated = roomEntity.toBuilder()
			.backgroundId(backgroundId)
			.build();

		RoomRedisEntity save = roomRedisRepository.save(updated);

		return save.getBackgroundId();
	}
}

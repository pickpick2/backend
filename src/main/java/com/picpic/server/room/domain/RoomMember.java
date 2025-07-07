package com.picpic.server.room.domain;

import com.picpic.server.common.auth.MemberPrincipalDetail;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RoomMember {
	Long memberId;
	String memberName;
	RoomMemberStatus memberStatus;

	public enum RoomMemberStatus {
		NONE, CONNECTED, DISCONNECTED
	}

	public static RoomMember from(MemberPrincipalDetail principalDetail) {
		return RoomMember.builder()
			.memberId(principalDetail.memberId())
			.memberName(principalDetail.getName())
			.build();
	}
}

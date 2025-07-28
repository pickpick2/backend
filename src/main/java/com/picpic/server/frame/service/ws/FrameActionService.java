package com.picpic.server.frame.service.ws;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.frame.dto.ws.FrameActionResponse;
import com.picpic.server.frame.dto.ws.FrameMessageType;
import com.picpic.server.frame.dto.ws.FrameWsMessage;
import com.picpic.server.frame.dto.ws.UserProfile;
import com.picpic.server.frame.service.FrameCellService;
import com.picpic.server.frame.service.FrameVoteService;
import com.picpic.server.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameActionService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final FrameCellService frameCellService;
	private final MemberRepository memberRepository;
	private final SimpMessagingTemplate template;
	private final FrameVoteService frameVoteService;

	@Transactional
	public void handleAction(
		String roomId, Long userId, Long targetId,
		FrameMessageType type, String requestId
	) {
		FrameActionResponse payload;
		if (type == FrameMessageType.VOTE) {
			payload = doVote(roomId, userId, targetId);
		} else {
			payload = doSelect(roomId, userId, targetId);
		}

		template.convertAndSend(
			"/topic/room/" + roomId + "/frames",
			new FrameWsMessage<>(type, requestId, payload)
		);
	}

	private FrameActionResponse doVote(String roomId, Long userId, Long frameId) {
		frameVoteService.vote(roomId, frameId, userId);

		Set<Long> voterIds = frameVoteService.getVoters(roomId, frameId);
		Set<Object> members = new HashSet<>(voterIds);
		List<UserProfile> profiles = mapToProfiles(members);

		return new FrameActionResponse(FrameMessageType.VOTE, profiles);
	}

	private FrameActionResponse doSelect(String roomId, Long userId, Long frameId) {
		// 새로 작성한 FrameCellService 사용
		boolean success = frameCellService.selectCell(roomId, frameId, userId.intValue(), userId);
		if (!success) {
			throw new IllegalStateException("이미 선택된 셀입니다.");
		}

		var selectionMap = frameCellService.getAllSelections(roomId, frameId);

		List<UserProfile> profiles = selectionMap.values().stream()
			.map(memberRepository::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(m -> new UserProfile(
				m.getMemberId(),
				m.getNickname(),
				m.getProfileImageUrl()
			)).toList();

		return new FrameActionResponse(FrameMessageType.SELECT, profiles);
	}

	private List<UserProfile> mapToProfiles(Set<Object> members) {
		return members.stream()
			.map(o -> Long.parseLong(o.toString()))
			.map(memberRepository::findById)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(m -> new UserProfile(
				m.getMemberId(),
				m.getNickname(),
				m.getProfileImageUrl()
			))
			.toList();
	}
}

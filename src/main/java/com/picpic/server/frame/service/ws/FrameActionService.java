package com.picpic.server.frame.service.ws;

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
import com.picpic.server.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FrameActionService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final MemberRepository memberRepository;
	private final SimpMessagingTemplate template;

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

	private FrameActionResponse doVote(String roomId, Long userId, Long newFrameId) {
		Set<String> voteKeys = redisTemplate.keys("room:" + roomId + ":frame:*:votes");
		if (voteKeys != null) {
			for (String key : voteKeys) {
				redisTemplate.opsForSet().remove(key, userId);
			}
		}
		String newKey = "room:" + roomId + ":frame:" + newFrameId + ":votes";
		redisTemplate.opsForSet().add(newKey, userId);
		@SuppressWarnings("unchecked")
		Set<Object> members = redisTemplate.opsForSet().members(newKey);
		List<UserProfile> profiles = mapToProfiles(members);

		return new FrameActionResponse(FrameMessageType.VOTE, profiles);
	}

	private FrameActionResponse doSelect(String roomId, Long userId, Long cellId) {
		String key = "room:" + roomId + ":cell:" + cellId + ":selections";
		redisTemplate.opsForSet().add(key, userId);

		@SuppressWarnings("unchecked")
		Set<Object> members = redisTemplate.opsForSet().members(key);
		List<UserProfile> profiles = mapToProfiles(members);

		return new FrameActionResponse(FrameMessageType.SELECT, profiles);
	}

	private List<UserProfile> mapToProfiles(Set<Object> members) {
		return members.stream()
			.map(o -> (Long)o)
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

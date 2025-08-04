package com.picpic.server.frame.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import com.picpic.server.frame.dto.FrameOptionResponse;
import com.picpic.server.frame.entity.Frame;
import com.picpic.server.frame.repository.FrameRepository;

@ExtendWith(MockitoExtension.class)
class FrameOptionServiceTest {

	@Mock
	private FrameRepository frameRepository;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private SetOperations<String, Object> setOperations;

	@InjectMocks
	private FrameOptionService frameOptionService;

	@Test
	@DisplayName("getFrameOptions - 참가자 수와 slotCount가 같은 프레임만 조회해서 DTO로 매핑한다")
	void getFrameOptions_filtersByParticipantCountAndMapsToDto() {
		Long roomId = 42L;
		String participantKey = "room:" + roomId + ":participants";

		Frame frameMatch = Frame.builder()
			.frameId(1L)
			.name("2x2")
			.slotCount(4)
			.frameImageUrl("url1")
			.deletedAt(null)
			.createdAt(LocalDateTime.now())
			.build();

		Frame frameNoMatch = Frame.builder()
			.frameId(2L)
			.name("1x3")
			.slotCount(3)
			.frameImageUrl("url2")
			.deletedAt(null)
			.createdAt(LocalDateTime.now())
			.build();

		given(frameRepository.findByDeletedAtIsNull())
			.willReturn(List.of(frameMatch, frameNoMatch));

		given(redisTemplate.opsForSet()).willReturn(setOperations);
		Set<Object> mockMembers = Set.of(10L, 20L, 30L, 40L);
		given(setOperations.members(participantKey)).willReturn(mockMembers);

		// when
		List<FrameOptionResponse> result = frameOptionService.getFrameOptions(roomId);

		// then
		then(frameRepository).should().findByDeletedAtIsNull();
		then(redisTemplate).should().opsForSet();
		then(setOperations).should().members(participantKey);

		// 오직 slotCount=4인 frameMatch만 반환되어야 한다
		assertThat(result).hasSize(1);
		FrameOptionResponse dto = result.get(0);
		assertThat(dto.frameId()).isEqualTo(1L);
		assertThat(dto.name()).isEqualTo("2x2");
		assertThat(dto.slotCount()).isEqualTo(4);
		assertThat(dto.frameImageUrl()).isEqualTo("url1");
	}
}

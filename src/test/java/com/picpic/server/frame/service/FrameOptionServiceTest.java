package com.picpic.server.frame.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.picpic.server.frame.dto.FrameOptionResponse;
import com.picpic.server.frame.entity.Frame;
import com.picpic.server.frame.repository.FrameRepository;

@ExtendWith(MockitoExtension.class)
class FrameOptionServiceTest {

	@Mock
	private FrameRepository frameRepository;

	@InjectMocks
	private FrameOptionService frameOptionService;

	@Test
	@DisplayName("활성화된 프레임만 조회해서 DTO로 매핑한다")
	void getAllFrameOptions_filtersDeletedAndMapsToDto() {
		Frame active1 = Frame.builder()
			.frameId(1L)
			.name("2x2")
			.slotCount(4)
			.frameImageUrl("url1")
			.deletedAt(null)
			.createdAt(LocalDateTime.now())
			.build();

		Frame active2 = Frame.builder()
			.frameId(2L)
			.name("1x4")
			.slotCount(4)
			.frameImageUrl("url2")
			.deletedAt(null)
			.createdAt(LocalDateTime.now())
			.build();

		given(frameRepository.findByDeletedAtIsNull())
			.willReturn(List.of(active1, active2));

		List<FrameOptionResponse> result = frameOptionService.getAllFrameOptions();

		then(frameRepository).should().findByDeletedAtIsNull();
		assertThat(result).hasSize(2);

		FrameOptionResponse dto1 = result.get(0);
		assertThat(dto1.frameId()).isEqualTo(1L);
		assertThat(dto1.name()).isEqualTo("2x2");
		assertThat(dto1.slotCount()).isEqualTo(4);
		assertThat(dto1.frameImageUrl()).isEqualTo("url1");

		FrameOptionResponse dto2 = result.get(1);
		assertThat(dto2.frameId()).isEqualTo(2L);
		assertThat(dto2.name()).isEqualTo("1x4");
		assertThat(dto2.slotCount()).isEqualTo(4);
		assertThat(dto2.frameImageUrl()).isEqualTo("url2");
	}
}

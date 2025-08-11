package com.picpic.server.frame.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picpic.server.frame.dto.FrameOptionResponse;
import com.picpic.server.frame.service.FrameOptionService;

@WebMvcTest(FrameOptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class FrameOptionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private FrameOptionService frameOptionService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET /api/room/{roomId}/frames - 200, 리스트 반환")
	void getFrameOptions_success() throws Exception {
		// given
		Long roomId = 42L;
		List<FrameOptionResponse> mockList = List.of(
			new FrameOptionResponse(1L, "2x2", 4, "url1"),
			new FrameOptionResponse(2L, "1x4", 4, "url2")
		);
		given(frameOptionService.getFrameOptions(roomId)).willReturn(mockList);

		// when & then
		mockMvc.perform(get("/api/room/{roomId}/frames", roomId)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].frameId").value(1))
			.andExpect(jsonPath("$[0].name").value("2x2"))
			.andExpect(jsonPath("$[1].frameImageUrl").value("url2"));

		then(frameOptionService).should().getFrameOptions(roomId);
	}
}

package com.picpic.server.photo.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import com.picpic.server.common.security.MemberPrincipalDetail;
import com.picpic.server.photo.dto.PhotoDetailResponse;
import com.picpic.server.photo.dto.PhotoListResponse;
import com.picpic.server.photo.service.usecase.PhotoUseCase;

@WebMvcTest(PhotoController.class)
class PhotoControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private PhotoUseCase useCase;

	/**
	 * MemberPrincipalDetail 을 Authentication 토큰에 담아 주입하는 헬퍼
	 */
	private Authentication auth(long memberId) {
		// record 생성자를 직접 사용합니다.
		MemberPrincipalDetail principal = new MemberPrincipalDetail(memberId, "nick" + memberId);
		return new UsernamePasswordAuthenticationToken(principal, null, List.of());
	}

	@Test
	void listAlbums_OK() throws Exception {
		PhotoListResponse dto = PhotoListResponse.builder()
			.photoId("x1")
			.title("T")
			.url("U")
			.createdAt(123L)
			.build();

		given(useCase.listPhotos(
			eq(1L),
			any(),
			any(Instant.class),
			anyInt()
		)).willReturn(List.of(dto));

		mvc.perform(get("/api/albums")
				.with(authentication(auth(1L)))
				.param("size", "1")
				.with(csrf()))    // CSRF 필터 우회 (GET은 선택)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].photoId").value("x1"));
	}

	@Test
	void getAlbum_OK() throws Exception {
		PhotoDetailResponse detail = PhotoDetailResponse.builder()
			.photoId("p2")
			.title("TT")
			.url("UU")
			.createdAt(456L)
			.participantIds(List.of(1L, 2L))
			.build();

		given(useCase.getPhotoDetail(eq("p2"), eq(1L)))
			.willReturn(detail);

		mvc.perform(get("/api/albums/p2")
				.with(authentication(auth(1L)))
				.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.photoId").value("p2"))
			.andExpect(jsonPath("$.participantIds[0]").value(1));
	}

	@Test
	void deleteAlbum_NoContent() throws Exception {
		willDoNothing().given(useCase).deletePhoto("p3", 1L);

		mvc.perform(delete("/api/albums/p3")
				.with(authentication(auth(1L)))
				.with(csrf()))
			.andExpect(status().isOk());
	}
}

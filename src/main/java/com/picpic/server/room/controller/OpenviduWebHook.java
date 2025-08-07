package com.picpic.server.room.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.picpic.server.room.service.usecase.UpdateOVMemberStateUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/openvidu")
@RequiredArgsConstructor
public class OpenviduWebHook {

	private final UpdateOVMemberStateUseCase updateOVMemberStateUseCase;

	@PostMapping
	public void webhookTest(@RequestBody String payload) throws JsonProcessingException {
		updateOVMemberStateUseCase.updateMemberState(payload);
	}
}

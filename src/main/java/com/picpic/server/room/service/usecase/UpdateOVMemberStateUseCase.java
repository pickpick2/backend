package com.picpic.server.room.service.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface UpdateOVMemberStateUseCase {

	void updateMemberState(String payload) throws JsonProcessingException;
}

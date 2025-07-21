package com.picpic.server.frame.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.picpic.server.frame.entity.Frame;

public interface FrameRepository extends JpaRepository<Frame, Long> {
	List<Frame> findByDeletedAtIsNull();
}
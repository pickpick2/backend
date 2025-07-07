package com.picpic.server.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.picpic.server.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}

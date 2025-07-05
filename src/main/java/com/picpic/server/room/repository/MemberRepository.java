package com.picpic.server.room.repository;

import com.picpic.server.room.entity.Member;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface MemberRepository extends JpaRepository<Member, Long> {
}

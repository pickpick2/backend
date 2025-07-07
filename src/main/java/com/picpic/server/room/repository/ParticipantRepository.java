package com.picpic.server.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.picpic.server.member.entity.Member;
import com.picpic.server.room.entity.Participant;
import com.picpic.server.room.entity.Session;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

	Optional<Participant> findBySessionAndMember(Session session, Member member);

}

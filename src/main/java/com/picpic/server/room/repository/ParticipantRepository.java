package com.picpic.server.room.repository;

import com.picpic.server.room.entity.Member;
import com.picpic.server.room.entity.Participant;
import com.picpic.server.room.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findBySessionAndMember(Session session, Member member);

}

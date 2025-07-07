package com.picpic.server.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.picpic.server.member.entity.Nickname;

public interface NicknameRepository extends JpaRepository<Nickname, Long> {
	@Query(value = """
		SELECT
			1 AS nickname_id,
			(SELECT adjective FROM nickname ORDER BY RAND() LIMIT 1) AS adjective,
			(SELECT noun FROM nickname ORDER BY RAND() LIMIT 1) AS noun
		""", nativeQuery = true)
	Nickname findRandom();
}

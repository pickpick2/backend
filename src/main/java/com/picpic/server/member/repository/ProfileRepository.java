package com.picpic.server.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.picpic.server.member.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
	@Query(value = "SELECT * FROM profile ORDER BY RAND() LIMIT 1", nativeQuery = true)
	Profile findRandom();
}

package com.picpic.server.common.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.picpic.server.common.auth.JwtTokenProvider;
import com.picpic.server.common.auth.dto.GuestLoginResponseDTO;
import com.picpic.server.member.entity.Member;
import com.picpic.server.member.entity.Nickname;
import com.picpic.server.member.entity.Profile;
import com.picpic.server.member.repository.MemberRepository;
import com.picpic.server.member.repository.NicknameRepository;
import com.picpic.server.member.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final ProfileRepository profileRepository;
	private final NicknameRepository nicknameRepository;

	public GuestLoginResponseDTO guestLogin() {
		Profile profile = profileRepository.findRandom();
		Nickname nickname = nicknameRepository.findRandom();

		Member guest = Member.builder()
			.nickname(nickname.getAdjective() + " " + nickname.getNoun())
			.color(profile.getColor())
			.role(Member.Role.GUEST)
			.profileImageUrl(profile.getProfileImageUrl())
			.build();

		memberRepository.save(guest);

		String token = jwtTokenProvider.generateToken(guest.getMemberId());

		return GuestLoginResponseDTO.builder()
			.memberId(guest.getMemberId())
			.accessToken(token)
			.build();
	}
}


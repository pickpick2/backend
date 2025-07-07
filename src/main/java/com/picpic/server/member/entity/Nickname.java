package com.picpic.server.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nickname")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nickname {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nickname_id")
	private Long nicknameId;

	@Column(nullable = false, length = 10)
	private String adjective;

	@Column(nullable = false, length = 10)
	private String noun;
}
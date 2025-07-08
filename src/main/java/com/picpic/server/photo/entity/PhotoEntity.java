package com.picpic.server.photo.entity;

import java.time.Instant;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "photos")
public class PhotoEntity {

	@Id
	private String photoId;

	private String title;
	private String url;
	private Instant createdAt;

	@OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PhotoParticipantEntity> participants;
}

package com.picpic.server.photo.entity;

import java.time.Instant;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "photo_album")
public class PhotoAlbumEntity {

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "photoId", column = @Column(name = "photo_id")),
		@AttributeOverride(name = "memberId", column = @Column(name = "member_id"))
	})
	private PhotoAlbumId id;   // { photoId, memberId }

	@Column(nullable = false, updatable = false)
	private Instant addedAt;   // 앨범에 담긴 시각 (optional)

	@MapsId("photoId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id", insertable = false, updatable = false)
	private PhotoEntity photo;

	public static PhotoAlbumEntity of(String photoId, long memberId) {
		return PhotoAlbumEntity.builder()
			.id(new PhotoAlbumId(photoId, memberId))
			.addedAt(Instant.now())
			.build();
	}
}

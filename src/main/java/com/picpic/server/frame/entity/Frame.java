package com.picpic.server.frame.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "frame")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Frame {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "frame_id")
	private Long frameId;

	@Column(nullable = false, length = 20)
	private String name;

	@Column(name = "slot_count", nullable = false)
	private Integer slotCount;

	@Column(name = "frame_image_url", nullable = false)
	private String frameImageUrl;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;
}


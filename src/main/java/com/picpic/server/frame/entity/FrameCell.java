package com.picpic.server.frame.entity;

import com.picpic.server.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "frame_cell")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FrameCell {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cell_id")
	private Long id;

	@Column(name = "cell_index", nullable = false)
	private int cellIndex;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FrameCellStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "frame_id", nullable = false)
	private Frame frame;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public void select(Member member) {
		this.member = member;
		this.status = FrameCellStatus.SELECTED;
	}

	public void assign(Member member) {
		this.member = member;
		this.status = FrameCellStatus.ASSIGNED;
	}

	public boolean isEmpty() {
		return this.member == null;
	}
}

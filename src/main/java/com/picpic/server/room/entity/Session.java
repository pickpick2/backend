package com.picpic.server.room.entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "session")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "session_code", nullable = false, length = 32, unique = true)
    private String sessionCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "password")
    private String password;

    @Column(nullable = false)
    private SessionStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Getter
    public enum SessionStatus {
        WAITING(0),
        FRAME(1),
        PHOTO(2),
        DECORATE(3),
        RESULT(4),
        FINISHED(5);

        private final int status;

        private SessionStatus(int status) {
            this.status = status;
        }
    }

    public void start() {
        this.status = SessionStatus.PHOTO;
    }

    public void decorate() {
        this.status = SessionStatus.DECORATE;
    }

    public void collage() {
        this.status = SessionStatus.RESULT;
    }

    public void finish() {
        this.status = SessionStatus.FINISHED;
    }
}


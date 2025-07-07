package com.picpic.server.photo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

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

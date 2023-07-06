package com.ai.gardening.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    private String name;

    @NonNull
    private boolean isBlocked;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AppUser creator;

    public Channel(String name, boolean isBlocked, AppUser creator) {
        this.name = name;
        this.isBlocked = isBlocked;
        this.creator = creator;
    }
}

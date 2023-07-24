package com.ai.gardening.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    @NonNull
    @JsonIgnore
    private AppUser owner;

    @ManyToMany(mappedBy = "joinedChannels")
    @JsonIgnore
    private List<AppUser> joinedAppUsers = new ArrayList<>() ;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    public Channel(String name, boolean isBlocked, AppUser owner) {
        this.name = name;
        this.isBlocked = isBlocked;
        this.owner = owner;
    }
}

package com.example.filteringquerydsl.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "player")
@Entity
public class Player implements Serializable {
    
    @Id
    @Column(name = "username")
    private String username;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "firstName")
    private String firstName;
    
    @Column(name = "lastName")
    private String lastName;
    
    @Column(name = "joined")
    private LocalDateTime joined;
    
    @Column(name = "friends")
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Player> friends;
    
    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER)
    private List<FriendRequest> sentFriendRequests;
    
    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER)
    private List<FriendRequest> receivedFriendRequests;
    
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    private List<GamePlayer> gamePlayers;
    
}
package com.example.filteringquerydsl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "game")
@Entity
public class Game implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long gameId;
    
    @Column(name = "date")
    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    private GameSet gameSet;
    
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<GamePlayer> gamePlayers;
    public enum Status {
        COMPLETED,
        IN_PROGRESS
    }
}


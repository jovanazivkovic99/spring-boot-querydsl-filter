package com.example.filteringquerydsl.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "game_player")
@Entity
@Getter
public class GamePlayer {
    
    @EmbeddedId
    private GamePlayerCompositeKey id;
    
    @ManyToOne
    @MapsId("gameId")
    @JsonBackReference
    private Game game;
    
    @ManyToOne
    @MapsId("username")
    @JsonBackReference
    private Player player;
    
    @Column(name = "is_winner")
    private boolean isWinner;
    
    @Column(name = "image")
    private byte[] image;
    
    @Column(name = "score") // added from Sheet
    private Integer score;
}

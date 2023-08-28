package com.example.filteringquerydsl.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerCompositeKey implements Serializable {
    @Column(name = "game_id")
    private Long gameId;
    
    @Column(name = "username")
    private String username;
}

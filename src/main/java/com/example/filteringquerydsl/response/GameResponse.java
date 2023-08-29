package com.example.filteringquerydsl.response;

import com.example.filteringquerydsl.model.Player;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GameResponse(Long gameId,
                           LocalDateTime datePlayed,
                           String gameStatus,
                           List<PlayerResponse> players,
                           WinnerResponse winner
                           ) {
    
}

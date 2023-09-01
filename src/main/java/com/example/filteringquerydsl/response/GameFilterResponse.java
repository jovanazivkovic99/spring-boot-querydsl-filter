package com.example.filteringquerydsl.response;

import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GameFilterResponse(Long gameId,
                                 LocalDateTime datePlayed,
                                 String gameStatus,
                                 List<PlayerFilterResponse> players,
                                 WinnerResponse winner
                           ) {
    
}
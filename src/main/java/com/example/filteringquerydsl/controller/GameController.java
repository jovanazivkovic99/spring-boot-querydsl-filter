package com.example.filteringquerydsl.controller;

import com.example.filteringquerydsl.response.GameFilterResponse;
import com.example.filteringquerydsl.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    
    @GetMapping("/filter")
    public Page<GameFilterResponse> findByOpponentUsername (
            @RequestParam(required = false) List<String> opponentUsernames,
            @RequestParam(required = false) LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo,
            @RequestParam(required = false) String gameStatus,
            @RequestParam(required = false) String winnerUsername,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "ALL") String sortDirection
    ) {
        Sort sort = Sort.unsorted(); // Default to unsorted
        if ("ASC".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Order.asc("date"));
        } else if ("DESC".equalsIgnoreCase(sortDirection)) {
            sort = Sort.by(Sort.Order.desc("date"));
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return gameService.filterGames(
                opponentUsernames, dateFrom, dateTo, gameStatus, winnerUsername, pageable);
    }
}
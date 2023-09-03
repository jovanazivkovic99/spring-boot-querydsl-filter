package com.example.filteringquerydsl.controller;

import com.example.filteringquerydsl.response.GameFilterResponse;
import com.example.filteringquerydsl.service.GameService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
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
            @RequestParam(required = false)@DateTimeFormat(pattern = "yyy-mm-dd") LocalDateTime dateFrom,
            @RequestParam(required = false) LocalDateTime dateTo,
            @RequestParam(required = false) String gameStatus,
            @RequestParam(required = false) List<String> winnerUsernames,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(required = false, defaultValue = "date") String sortField,  // Default to "date"
            @RequestParam(required = false, defaultValue = "desc") String sortDirection  // Default to "asc"
    ) {
        // Parse the sort direction parameter
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);

        // Create a Sort object based on sortField and sortDirection
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        return gameService.filterGames(
                opponentUsernames, dateFrom, dateTo, gameStatus, winnerUsernames, pageable);
    }
}
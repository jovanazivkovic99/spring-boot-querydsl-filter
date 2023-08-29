package com.example.filteringquerydsl.controller;

import com.example.filteringquerydsl.model.Game;
import com.example.filteringquerydsl.model.GamePlayer;
import com.example.filteringquerydsl.request.GameFilterRequest;
import com.example.filteringquerydsl.response.GameResponse;
import com.example.filteringquerydsl.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.filteringquerydsl.model.QGame.game;
import static com.example.filteringquerydsl.model.QGamePlayer.gamePlayer;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    
    @PostMapping("/filter")
    public List<GameResponse> findByOpponentUsername(@RequestBody GameFilterRequest gameFilterRequest) {
        return gameService.filterGames(gameFilterRequest);
    }
    
    @GetMapping("/test")
    public List<GamePlayer> findGamesByOpponents(@RequestParam String username){
        return gameService.findGamesByOpponents(username);
    }
}

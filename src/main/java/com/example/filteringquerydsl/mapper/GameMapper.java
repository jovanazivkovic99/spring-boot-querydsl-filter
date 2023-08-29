package com.example.filteringquerydsl.mapper;

import com.example.filteringquerydsl.model.Game;
import com.example.filteringquerydsl.model.GamePlayer;
import com.example.filteringquerydsl.response.GameResponse;
import com.example.filteringquerydsl.response.PlayerResponse;
import com.example.filteringquerydsl.response.WinnerResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GameMapper {
    
    @Mapping(target = "gameId", source = "game.gameId")
    @Mapping(target = "datePlayed", source = "game.date")
    @Mapping(target = "gameStatus", source = "game.status")
    @Mapping(target = "players", expression = "java(mapPlayers(game.getGamePlayers()))")
    @Mapping(target = "winner", expression = "java(mapWinner(game))")
    GameResponse convertGameToGameResponse (Game game);
    
    default List<PlayerResponse> mapPlayers(List<GamePlayer> gamePlayers) {
        return gamePlayers.stream()
                          .map(this::mapGamePlayerToPlayerResponse)
                          .collect(Collectors.toList());
    }
    default PlayerResponse mapGamePlayerToPlayerResponse(GamePlayer gamePlayer) {
        return PlayerResponse.builder()
                             .username(gamePlayer.getPlayer().getUsername())
                             .image(gamePlayer.getImage())
                             .build();
    }
    
    default WinnerResponse mapWinner(Game game) {
        GamePlayer winner = game.getGamePlayers().stream()
                                .filter(GamePlayer::isWinner)
                                .findFirst()
                                .orElse(null);
        
        if (winner != null) {
            return WinnerResponse.builder()
                                 .firstName(winner.getPlayer().getFirstName())
                                 .lastName(winner.getPlayer().getLastName())
                                 .username(winner.getPlayer().getUsername())
                                 .image(winner.getImage())
                                 .build();
        } else {
            return null; // No winner found
        }
    }
}

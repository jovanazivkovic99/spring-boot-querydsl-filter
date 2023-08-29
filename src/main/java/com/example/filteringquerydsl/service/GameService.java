package com.example.filteringquerydsl.service;

import com.example.filteringquerydsl.mapper.GameMapper;
import com.example.filteringquerydsl.model.Game;
import com.example.filteringquerydsl.model.GamePlayer;
import com.example.filteringquerydsl.request.GameFilterRequest;
import com.example.filteringquerydsl.response.GameResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.filteringquerydsl.model.QGame.game;
import static com.example.filteringquerydsl.model.QGamePlayer.gamePlayer;
import static com.example.filteringquerydsl.model.QGameSet.gameSet;
import static com.example.filteringquerydsl.model.QPlayer.player;

@Service
@RequiredArgsConstructor
public class GameService {
    
    private final JPAQueryFactory jpaQueryFactory;
    private final GameMapper gameMapper;
    
    public List<GameResponse> filterGames (GameFilterRequest gameFilterRequest) {
        JPAQuery<Game> query = jpaQueryFactory.selectFrom(game)
                                              .leftJoin(game.gamePlayers, gamePlayer)
                                              .fetchJoin()
                                              .leftJoin(gamePlayer.player, player)
                                              .fetchJoin()
                                              .leftJoin(gameSet.games, game)
                                              .fetchJoin()
                                              .leftJoin(game.gameSet, gameSet);
        
        if (gameFilterRequest.dateFrom() != null) {
            query = query.where(game.date.after(gameFilterRequest.dateFrom())
                                         .or(game.date.eq(gameFilterRequest.dateFrom()))); // Include games with the
            // exact
            // starting date
        }
        
        if (gameFilterRequest.dateTo() != null) {
            query = query.where(game.date.before(gameFilterRequest.dateTo())
                                         .or(game.date.eq(gameFilterRequest.dateTo()))); // Include games with the exact
            // ending date
        }
        
        if (gameFilterRequest.opponentUsernames() != null && ! gameFilterRequest.opponentUsernames()
                                                                                .isEmpty()) {
            query = query.where(player.username.in(gameFilterRequest.opponentUsernames()));
        }
        if (gameFilterRequest.gameStatus() != null && ! gameFilterRequest.gameStatus()
                                                                         .isEmpty()) {
            query = query.where(gameSet.name.in(gameFilterRequest.gameStatus()));
        }
        
        List<Game> games = query.fetch();
        return games.stream()
                    .map(gameMapper::convertGameToGameResponse)
                    .collect(Collectors.toList());
    }
    
    public List<GamePlayer> findGamesByOpponents (String username) {
        JPAQuery<GamePlayer> query = jpaQueryFactory.selectFrom(gamePlayer)
                                                    .where(gamePlayer.player.username.eq(username));
        
        if (username != null && ! username.isEmpty()) {
            query = query.where(player.username.in(username));
            
        }
        return query.fetch();
    }
}

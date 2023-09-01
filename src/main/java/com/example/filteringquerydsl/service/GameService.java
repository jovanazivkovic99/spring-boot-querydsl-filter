package com.example.filteringquerydsl.service;

import com.example.filteringquerydsl.mapper.GameMapper;
import com.example.filteringquerydsl.model.Game;
import com.example.filteringquerydsl.model.QGame;
import com.example.filteringquerydsl.model.QGamePlayer;
import com.example.filteringquerydsl.model.QPlayer;
import com.example.filteringquerydsl.response.GameFilterResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.filteringquerydsl.model.QGame.game;
import static com.example.filteringquerydsl.model.QGamePlayer.gamePlayer;
import static com.example.filteringquerydsl.model.QPlayer.player;

@Service
@RequiredArgsConstructor
public class GameService {
    
    private final JPAQueryFactory jpaQueryFactory;
    private final GameMapper gameMapper;
    
    public Page<GameFilterResponse> filterGames (List<String> opponentUsernames, LocalDateTime dateFrom,
                                                 LocalDateTime dateTo,
                                                 String gameStatus,
                                                 String winnerUsername, Pageable pageable) {
        JPAQuery<Game> query = jpaQueryFactory.selectFrom(game);
        
        // Apply filters
        query = applyFilters(query, dateFrom, dateTo, opponentUsernames, gameStatus, winnerUsername);
        
        long totalElements = query.distinct().orderBy(getSortedColumn(pageable.getSort())).fetchCount();
        
        // Apply pagination
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        
        List<Game> filteredGames = query.fetch();
        List<GameFilterResponse> gameFilterResponses = filteredGames.stream()
                                                                    .map(gameMapper::convertGameToGameResponse)
                                                                    .collect(Collectors.toList());
        
        return new PageImpl<>(gameFilterResponses, pageable, totalElements);
    }
    private OrderSpecifier<?>[] getSortedColumn(Sort sorts){
        return sorts.toList().stream().map(x ->{
            Order order = x.getDirection().name() == "ASC"? Order.ASC : Order.DESC;
            SimplePath<Game> filedPath = Expressions.path(Game.class, game, x.getProperty());
            return new OrderSpecifier(order, filedPath);
        }).toArray(OrderSpecifier[]::new);
    }
    /*private OrderSpecifier<?> parseSort(Sort.Order order) {
        if (order.isAscending()) {
            return Expressions.path(Object.class, game, order.getProperty()).asc();
        } else {
            return Expressions.path(Object.class, game, order.getProperty()).desc();
        }
    }*/
    
    
    private JPAQuery<Game> applyFilters (JPAQuery<Game> query, LocalDateTime dateFrom, LocalDateTime dateTo,
                                         List<String> opponentUsernames, String gameStatus, String winnerUsername) {
        query = filterByDate(query, dateFrom, dateTo);
        query =
                filterByOpponentUsernames(query, opponentUsernames)/*.offset(pageable.getOffset()).limit(pageable
                .getPageSize())*/;
        query = filterByStatus(query, gameStatus);
        query = filterByWinners(query, winnerUsername);
        return query;
    }
    
    private JPAQuery<Game> filterByDate (JPAQuery<Game> query, LocalDateTime dateFrom, LocalDateTime dateTo) {
        if (dateFrom != null) {
            query = query.where(game.date.after(dateFrom)
                                         .or(game.date.eq(dateFrom)));
        }
        if (dateTo != null) {
            query = query.where(game.date.before(dateTo)
                                         .or(game.date.eq(dateTo)));
        }
        return query;
    }
    
    private JPAQuery<Game> filterByOpponentUsernames (JPAQuery<Game> query, List<String> usernames) {
        if (usernames != null && ! usernames.isEmpty()) {
            
            // Mora da se doda ovaj deo jer se u suprotnom javalja greska AliasCollisionException ako se filtriraju
            // npr oppUsernames i winnerUsername u isto vreme.
            QGamePlayer opponentGamePlayer = new QGamePlayer("opponentGamePlayer");
            QPlayer opponentPlayer = new QPlayer("opponentPlayer");
            query = query
                    .leftJoin(game.gamePlayers, opponentGamePlayer)
                    .leftJoin(opponentGamePlayer.player, opponentPlayer)
                    .where(opponentPlayer.username.in(usernames));
        }
        return query;
    }
    
    
    private JPAQuery<Game> filterByStatus (JPAQuery<Game> query, String gameStatus) {
        if (gameStatus != null) {
            query = query.where(game.status.eq(Game.Status.valueOf(gameStatus)));
        }
        return query;
    }
    
    private JPAQuery<Game> filterByWinners (JPAQuery<Game> query, String winnerUsername) {
        
        
        if (winnerUsername != null && ! winnerUsername.isEmpty()) {
            QGamePlayer winnerGamePlayer = new QGamePlayer("winnerGamePlayer");
            QPlayer winnerPlayer = new QPlayer("winnerPlayer");
            
            query = query.leftJoin(game.gamePlayers, winnerGamePlayer)
                         .leftJoin(winnerGamePlayer.player, winnerPlayer)
                         .where(winnerGamePlayer.isWinner.eq(true)
                                                   .and(winnerPlayer.username.eq(winnerUsername)));
        }
        return query;
    }
    
}

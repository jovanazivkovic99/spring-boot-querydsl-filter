package com.example.filteringquerydsl.service;

import com.example.filteringquerydsl.mapper.GameMapper;
import com.example.filteringquerydsl.model.Game;
import com.example.filteringquerydsl.model.QGamePlayer;
import com.example.filteringquerydsl.model.QPlayer;
import com.example.filteringquerydsl.response.GameFilterResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.filteringquerydsl.model.QGame.game;

@Service
@RequiredArgsConstructor
public class GameService {

    private final JPAQueryFactory jpaQueryFactory;
    private final GameMapper gameMapper;

    /**
     * Filters and paginates games based on various criteria.
     *
     * @param opponentUsernames List of opponent usernames to filter by.
     * @param dateFrom          The starting game date range for filtering.
     * @param dateTo            The ending game date range for filtering.
     * @param gameStatus        The game status to filter by.
     * @param winnerUsernames    List of winner usernames to filter by.
     * @param pageable          Pageable object containing pagination and sorting information.
     * @return A Page of {@link GameFilterResponse} containing the filtered and paginated results.
     */
    public Page<GameFilterResponse> filterGames(List<String> opponentUsernames, LocalDateTime dateFrom,
                                                LocalDateTime dateTo,
                                                String gameStatus,
                                                List<String> winnerUsernames, Pageable pageable) {
        JPAQuery<Game> query = jpaQueryFactory.selectFrom(game);

        // Apply filters
        query = applyFilters(query, dateFrom, dateTo, opponentUsernames, gameStatus, winnerUsernames);


        Sort sort = pageable.getSort();
        OrderSpecifier<?>[] orderSpecifiers = getSortedColumn(sort);
        // Calculate the total number of elements
        long totalElements = query.distinct().fetchCount();

        //Apply pagination and sorting
        query.orderBy(orderSpecifiers);
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        // Fetch the filtered games
        List<Game> filteredGames = query.fetch();


        List<GameFilterResponse> gameFilterResponses = filteredGames.stream()
                .map(gameMapper::convertGameToGameResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(gameFilterResponses, pageable, totalElements);
    }

    /**
     * Converts a list of Sort objects to an array of OrderSpecifier objects for sorting.
     *
     * @param sorts The list of Sort objects representing sorting criteria.
     * @return An array of OrderSpecifier objects for sorting.
     */
    private OrderSpecifier<?>[] getSortedColumn(Sort sorts) {
        return sorts.toList().stream().map(x -> {
            Order order = x.getDirection().name() == "ASC" ? Order.ASC : Order.DESC;
            SimplePath<Game> filedPath = Expressions.path(Game.class, game, x.getProperty());
            return new OrderSpecifier(order, filedPath);
        }).toArray(OrderSpecifier[]::new);
    }

    /**
     * Applies filters to the JPAQuery based on various criteria.
     *
     * @param query             The JPAQuery instance to apply the filters to.
     * @param dateFrom          The starting date range for filtering.
     * @param dateTo            The ending date range for filtering.
     * @param opponentUsernames List of opponent usernames to filter by.
     * @param gameStatus        The game status to filter by.
     * @param winnerUsernames    The winner's username to filter by.
     * @return The modified JPAQuery with filters applied.
     */
    private JPAQuery<Game> applyFilters(JPAQuery<Game> query, LocalDateTime dateFrom, LocalDateTime dateTo,
                                        List<String> opponentUsernames, String gameStatus, List<String> winnerUsernames) {
        query = filterByDate(query, dateFrom, dateTo);
        query =
                filterByOpponentUsernames(query, opponentUsernames);
        query = filterByStatus(query, gameStatus);
        query = filterByWinners(query, winnerUsernames);
        return query;
    }

    /**
     * Applies filtering based on the date range.
     *
     * @param query    The JPAQuery instance to apply the filter to.
     * @param dateFrom The starting game date range for filtering.
     * @param dateTo   The ending game date range for filtering.
     * @return The modified JPAQuery with the date filter applied.
     */
    private JPAQuery<Game> filterByDate(JPAQuery<Game> query, LocalDateTime dateFrom, LocalDateTime dateTo) {
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

    /**
     * Applies filtering based on opponent usernames.
     *
     * @param query     The JPAQuery instance to apply the filter to.
     * @param usernames The list of opponent usernames to filter by.
     * @return The modified JPAQuery with the opponent username filter applied.
     */
    private JPAQuery<Game> filterByOpponentUsernames(JPAQuery<Game> query, List<String> usernames) {
        if (usernames != null && !usernames.isEmpty()) {

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

    /**
     * Applies filtering based on game status.
     *
     * @param query      The JPAQuery instance to apply the filter to.
     * @param gameStatus The game status to filter by.
     * @return The modified JPAQuery with the game status filter applied.
     */
    private JPAQuery<Game> filterByStatus(JPAQuery<Game> query, String gameStatus) {
        if (gameStatus != null) {
            query = query.where(game.status.eq(Game.Status.valueOf(gameStatus)));
        }
        return query;
    }

    /**
     * Applies filtering based on game winners' usernames.
     *
     * @param query          The JPAQuery instance to apply the filter to.
     * @param winnerUsernames List of winner usernames to filter by.
     * @return The modified JPAQuery with the winner username filter applied.
     */
    private JPAQuery<Game> filterByWinners(JPAQuery<Game> query, List<String> winnerUsernames) {


        if (winnerUsernames != null && !winnerUsernames.isEmpty()) {
            // Mora da se doda ovaj deo jer se u suprotnom javalja greska AliasCollisionException ako se filtriraju
            // npr oppUsernames i winnerUsername u isto vreme.
            QGamePlayer winnerGamePlayer = new QGamePlayer("winnerGamePlayer");
            QPlayer winnerPlayer = new QPlayer("winnerPlayer");

            query = query.leftJoin(game.gamePlayers, winnerGamePlayer)
                    .leftJoin(winnerGamePlayer.player, winnerPlayer)
                    .where(winnerGamePlayer.isWinner.eq(true)
                            .and(winnerPlayer.username.in(winnerUsernames)));
        }
        return query;
    }

}

package com.example.filteringquerydsl.request;

import lombok.Builder;
import org.springframework.cglib.core.Local;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GameFilterRequest(List<String> opponentUsernames,
                                LocalDateTime dateFrom,
                                LocalDateTime dateTo,
                                String gameStatus) {

}
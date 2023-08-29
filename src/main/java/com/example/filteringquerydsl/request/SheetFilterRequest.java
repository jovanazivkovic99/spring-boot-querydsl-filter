package com.example.filteringquerydsl.request;

import lombok.Builder;

@Builder
public record SheetFilterRequest(Integer minScore, Integer maxScore) {

}

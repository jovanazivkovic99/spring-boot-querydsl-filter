package com.example.filteringquerydsl.response;

import lombok.Builder;

@Builder
public record SheetResponse(Long id,
                            Integer score,
                            String name,
                            GameResponse gameResponse) {
    
}

package com.example.filteringquerydsl.response;

import lombok.Builder;

@Builder
public record WinnerResponse(String firstName,
                             String lastName,
                             String username) {

}

package com.example.filteringquerydsl.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PlayerResponse(String username, byte[] image) {

}

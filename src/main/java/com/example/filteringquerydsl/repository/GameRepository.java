package com.example.filteringquerydsl.repository;

import com.example.filteringquerydsl.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

}

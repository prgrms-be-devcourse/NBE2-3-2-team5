package com.example.festimo.domain.festival.repository;

import com.example.festimo.domain.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, String> {
}

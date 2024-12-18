package com.example.festimo.domain.festival.repository;

import com.example.festimo.domain.festival.domain.Festival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FestivalRepository extends JpaRepository<Festival, String> {
    List<Festival> findByTitleContainingIgnoreCase(String keyword);

    List<Festival> findByAddressContainingIgnoreCase(String region);
}

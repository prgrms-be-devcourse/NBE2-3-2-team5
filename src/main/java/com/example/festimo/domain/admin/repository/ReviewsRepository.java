package com.example.festimo.domain.admin.repository;

import com.example.festimo.domain.admin.entity.Reviews;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

    @NonNull
    Page<Reviews> findAll(@NonNull Pageable pageable);
}

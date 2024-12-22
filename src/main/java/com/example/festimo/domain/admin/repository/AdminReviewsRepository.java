package com.example.festimo.domain.admin.repository;

import com.example.festimo.domain.admin.entity.AdminReviews;

import lombok.NonNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminReviewsRepository extends JpaRepository<AdminReviews, Long> {

    @NonNull
    Page<AdminReviews> findAll(@NonNull Pageable pageable);
}

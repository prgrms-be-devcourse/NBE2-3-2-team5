package com.example.festimo.domain.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.NonNull;

import com.example.festimo.domain.admin.entity.Users;

public interface UserRepository extends JpaRepository<Users,Long> {
    @NonNull
    Page<Users> findAll(@NonNull Pageable pageable);

}

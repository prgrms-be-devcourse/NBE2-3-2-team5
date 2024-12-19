package com.example.festimo.domain.user.repository;

import com.example.festimo.domain.user.entity.Users;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users,Long> {
    @NonNull
    Page<Users> findAll(@NonNull Pageable pageable);
}

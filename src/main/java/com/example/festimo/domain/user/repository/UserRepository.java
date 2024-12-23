package com.example.festimo.domain.user.repository;


import com.example.festimo.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByRefreshToken(String refreshToken);

    User findByUserName(String username);
}



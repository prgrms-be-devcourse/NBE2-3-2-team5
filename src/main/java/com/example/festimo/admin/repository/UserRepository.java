package com.example.festimo.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.festimo.admin.domain.Users;

public interface UserRepository extends JpaRepository<Users,Long> {

}

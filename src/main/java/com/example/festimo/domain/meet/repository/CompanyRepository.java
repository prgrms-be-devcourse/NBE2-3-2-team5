package com.example.festimo.domain.meet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.Companies;

@Repository
public interface CompanyRepository extends JpaRepository<Companies, Long> {
}
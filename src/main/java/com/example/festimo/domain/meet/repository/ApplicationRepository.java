package com.example.festimo.domain.meet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.Applications;

@Repository
public interface ApplicationRepository extends JpaRepository<Applications, Long> {
    boolean existsByUserIdAndCompanyId(Long userId, Long companyId);
}
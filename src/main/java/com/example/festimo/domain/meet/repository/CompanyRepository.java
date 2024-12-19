package com.example.festimo.domain.meet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.Companies;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Companies, Long> {
    Optional<Companies> findByCompanyId(Long CompanyId);

    @Query("SELECT c.leaderId FROM Companies c WHERE c.companyId = :companyId")
    Optional<Long> findLeaderIdByCompanyId( Long companyId);

}
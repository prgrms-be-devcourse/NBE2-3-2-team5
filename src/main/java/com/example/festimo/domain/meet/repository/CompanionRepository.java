package com.example.festimo.domain.meet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.Companion;

import java.util.Optional;

@Repository
public interface CompanionRepository extends JpaRepository<Companion, Long> {

    // 동행의 리더 찾기
    @Query("SELECT c.leaderId FROM Companion c WHERE c.companionId = :companyId")
    Optional<Long> findLeaderIdByCompanyId( Long companyId);

}
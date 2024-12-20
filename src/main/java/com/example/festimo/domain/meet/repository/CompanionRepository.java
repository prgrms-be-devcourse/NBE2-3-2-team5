package com.example.festimo.domain.meet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.CompanionId;
import com.example.festimo.domain.meet.entity.Companions;

@Repository
public interface CompanionRepository extends JpaRepository<Companions, CompanionId> {

    boolean existsById(CompanionId companionId);  // 참가 여부 확인

}

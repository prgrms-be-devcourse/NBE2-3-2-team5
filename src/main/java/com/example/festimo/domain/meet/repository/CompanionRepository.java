package com.example.festimo.domain.meet.repository;


import com.example.festimo.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.Companion;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanionRepository extends JpaRepository<Companion, Long> {


    // 리더 ID로 모임 조회
    List<Companion> findByLeaderId(Long leaderId);

        //중복체크
    Optional<Companion> findByPost(Post post);

    // 특정 Companion ID와 User ID가 존재하는지 확인
    boolean existsByCompanionIdAndLeaderId(Long companionId, Long leaderId);

    @Query("SELECT c.leaderId FROM Companion c WHERE c.companionId = :companionId")
    Optional<Long> findLeaderIdByCompanyId(Long companionId);
}
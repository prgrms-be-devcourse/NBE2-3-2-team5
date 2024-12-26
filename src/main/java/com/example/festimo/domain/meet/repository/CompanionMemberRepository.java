package com.example.festimo.domain.meet.repository;


import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.entity.Companion_member;

import java.util.List;

@Repository
public interface CompanionMemberRepository extends JpaRepository<Companion_member, CompanionMemberId> {

    // 참가 여부 확인
    boolean existsById(@NonNull CompanionMemberId companionMemberId);


    // 특정 사용자가 멤버로 포함된 동행 조회
    @Query("SELECT cm FROM Companion_member cm JOIN FETCH cm.companion WHERE cm.id.userId = :userId")
    List<Companion_member> findByUserId(@Param("userId") Long userId);

    // 특정 동행에 포함된 모든 멤버 조회
    @Query("SELECT cm FROM Companion_member cm JOIN FETCH cm.user WHERE cm.id.companionId = :companionId")
    List<Companion_member> findAllByCompanionId(@Param("companionId") Long companionId);



}

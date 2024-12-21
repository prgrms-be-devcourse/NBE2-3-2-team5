package com.example.festimo.domain.meet.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.entity.Companion_member;

@Repository
public interface CompanionMemberRepository extends JpaRepository<Companion_member, CompanionMemberId> {

    boolean existsById(@NonNull CompanionMemberId companionMemberId);  // 참가 여부 확인

}

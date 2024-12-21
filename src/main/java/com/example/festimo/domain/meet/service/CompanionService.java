package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.repository.CompanionMemberRepository;
import com.example.festimo.exception.CustomException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static com.example.festimo.exception.ErrorCode.COMPANION_NOT_FOUND;

@Service
public class CompanionService {

    private final CompanionMemberRepository companionMemberRepository;

    public CompanionService(CompanionMemberRepository companionMemberRepository) {
        this.companionMemberRepository = companionMemberRepository;
    }

    /**
     * 동행 취소
     *
     * @param companionId  취소하고 싶은 동행ID
     * @param userId     취소하고 싶은 듀저 ID
     */
    @Transactional
    public void deleteCompaion(Long companionId, Long userId) {

        CompanionMemberId companionMemberId = new CompanionMemberId(companionId, userId);

        //동행에 참가 여부 확인
        if(!companionMemberRepository.existsById(companionMemberId)){
            throw new CustomException(COMPANION_NOT_FOUND);
        }

        //동행원에서 삭제
        companionMemberRepository.deleteById(companionMemberId);
    }

}

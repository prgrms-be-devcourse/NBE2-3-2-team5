package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.entity.CompanionId;
import com.example.festimo.domain.meet.repository.CompanionRepository;
import com.example.festimo.exception.CustomException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import static com.example.festimo.exception.ErrorCode.COMPANION_NOT_FOUND;

@Service
public class CompanionService {

    private final CompanionRepository companionRepository;

    public CompanionService(CompanionRepository companionRepository) {
        this.companionRepository = companionRepository;
    }

    /**
     * 동행 취소
     *
     * @param companyId  취소하고 싶은 동행ID
     * @param userId     취소하고 싶은 듀저 ID
     */
    @Transactional
    public void deleteCompaion(Long companyId, Long userId) {

        CompanionId companionId = new CompanionId(companyId, userId);

        //동행에 참가 여부 확인
        if(!companionRepository.existsById(companionId)){
            throw new CustomException(COMPANION_NOT_FOUND);
        }

        //동행원에서 삭제
        companionRepository.deleteById(companionId);
    }

}

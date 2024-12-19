package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.entity.Companies;
import com.example.festimo.domain.meet.entity.CompanionId;
import com.example.festimo.domain.meet.entity.Companions;
import com.example.festimo.domain.meet.repository.CompanionRepository;

import com.example.festimo.domain.meet.dto.ApplicationResponse;
import com.example.festimo.domain.meet.dto.LeaderApplicationResponse;
import com.example.festimo.domain.meet.entity.Applications;
import com.example.festimo.domain.meet.mapper.ApplicationMapper;
import com.example.festimo.domain.meet.mapper.LeaderApplicationMapper;
import com.example.festimo.domain.meet.repository.ApplicationRepository;
import com.example.festimo.domain.meet.repository.CompanyRepository;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.CustomException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.festimo.exception.ErrorCode.*;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanionRepository companionRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            CompanionRepository companionRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.companionRepository = companionRepository;
    }

    /**
     * 신청 생성
     *
     * @param userId    신청을 생성하는 유저의 ID
     * @param companyId 신청 대상 회사의 ID
     * @return 생성된 신청 정보
     */
    public ApplicationResponse createApplication(Long userId, Long companyId) {

        // userId 확인
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new CustomException(USER_NOT_FOUND);
        }

        // companyId 존재 확인
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new CustomException(COMPANY_NOT_FOUND);
        }

        // 이미 신청이 있는지 확인
        boolean applicationExists = applicationRepository.existsByUserIdAndCompanyId(userId, companyId);
        if (applicationExists) {
            throw new CustomException(DUPLICATE_APPLICATION);
        }

        Applications application = new Applications(userId, companyId);
        application = applicationRepository.save(application);

        return ApplicationMapper.INSTANCE.toDto(application);
    }

    /**
     * 신청 리스트 확인
     *
     * @param companyId 확인하려는 회사의 ID
     * @param userId    신청 리스트를 확인하려는 리더의 ID
     * @return 신청 리스트 정보
     */
    public List<LeaderApplicationResponse> getAllApplications(Long companyId, Long userId) {

        // 리더인지 확인
        Long company = companyRepository.findLeaderIdByCompanyId(companyId)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        if (!userId.equals(company)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 리스트 확인
        List<Applications> applications = applicationRepository.findByCompanyId(companyId);
        return LeaderApplicationMapper.INSTANCE.toDtoList(applications);
    }

    /**
     * 리더의 신청 승인
     *
     * @param applicationId 승인하고 싶은 신청 ID
     * @param userId        신청을 승인하려는 리더의 ID
     */
    @Transactional
    public void acceptApplication(Long applicationId, Long userId) {

        // 신청 ID로 조회
        Applications application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(APPLICATION_NOT_FOUND));

        // 리더인지 확인
        Long companyId = application.getCompanyId();
        Long company = companyRepository.findLeaderIdByCompanyId(companyId)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        if (!userId.equals(company)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 상태 바꾸기
        if (!application.getStatus().equals(Applications.Status.PENDING)) {
            throw new CustomException(INVALID_APPLICATION_STATUS);
        }
        application.setStatus(Applications.Status.ACCEPTED);
        applicationRepository.save(application);

        // 동행에 추가
        CompanionId companionId = new CompanionId(application.getUserId(), application.getCompanyId());
        Companions companions = new Companions(companionId, LocalDateTime.now());
        companionRepository.save(companions);
    }

    /**
     * 리더의 신청 거절
     *
     * @param applicationId 거절하고 싶은 신청 ID
     * @param userId        신청을 거절하려는 리더의 ID
     */
    public void rejectApplication(Long applicationId, Long userId) {

        // 신청 ID로 조회
        Applications application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(APPLICATION_NOT_FOUND));

        // 리더인지 확인
        Long companyId = application.getCompanyId();
        Long company = companyRepository.findLeaderIdByCompanyId(companyId)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        if (!userId.equals(company)) {
            throw new CustomException(ACCESS_DENIED);
        }

        // 상태 바꾸기
        if (!application.getStatus().equals(Applications.Status.PENDING)) {
            throw new CustomException(INVALID_APPLICATION_STATUS);
        }
        application.setStatus(Applications.Status.REJECTED);
        applicationRepository.save(application);
    }
}

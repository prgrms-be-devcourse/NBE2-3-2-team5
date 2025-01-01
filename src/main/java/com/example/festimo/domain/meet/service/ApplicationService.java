package com.example.festimo.domain.meet.service;

import com.example.festimo.domain.meet.entity.CompanionMemberId;
import com.example.festimo.domain.meet.entity.Companion_member;
import com.example.festimo.domain.meet.repository.CompanionMemberRepository;

import com.example.festimo.domain.meet.dto.ApplicationResponse;
import com.example.festimo.domain.meet.dto.LeaderApplicationResponse;
import com.example.festimo.domain.meet.entity.Applications;
import com.example.festimo.domain.meet.mapper.ApplicationMapper;
import com.example.festimo.domain.meet.mapper.LeaderApplicationMapper;
import com.example.festimo.domain.meet.repository.ApplicationRepository;
import com.example.festimo.domain.meet.repository.CompanionRepository;
import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.CustomException;
import com.example.festimo.domain.user.dto.UserNicknameProjection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.festimo.exception.ErrorCode.*;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CompanionRepository companionRepository;
    private final UserRepository userRepository;
    private final CompanionMemberRepository companionMemberRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            CompanionRepository companionRepository,
            UserRepository userRepository,
            CompanionMemberRepository companionMemberRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.companionRepository = companionRepository;
        this.userRepository = userRepository;
        this.companionMemberRepository = companionMemberRepository;
    }

    /**
     * 신청 생성
     *
     * @param userId    신청을 생성하는 유저의 ID
     * @param companionId 신청 동행 ID
     * @return 생성된 신청 정보
     */
    public ApplicationResponse createApplication(Long userId, Long companionId) {

        // userId 확인
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new CustomException(USER_NOT_FOUND);
        }

        // companionId 존재 확인
        boolean companyExists = companionRepository.existsById(companionId);
        if (!companyExists) {
            throw new CustomException(COMPANY_NOT_FOUND);
        }

        // 이미 신청이 있는지 확인
        boolean applicationExists = applicationRepository.existsByUserIdAndCompanionId(userId, companionId);
        if (applicationExists) {
            throw new CustomException(DUPLICATE_APPLICATION);
        }

        Applications application = new Applications(userId, companionId);
        application = applicationRepository.save(application);

        return ApplicationMapper.INSTANCE.toDto(application);
    }

    /**
     * 신청 리스트 확인
     *
     * @param companionId 확인하려는 동행의 ID
   //  * @param userId    신청 리스트를 확인하려는 리더의 ID
     * @return 신청 리스트 정보
     */
    public List<LeaderApplicationResponse> getAllApplications(Long companionId) {

        /*
        // 리더인지 확인->jwt
        Long company = companionRepository.findLeaderIdByCompanyId(companyId)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        if (!userId.equals(company)) {
            throw new CustomException(ACCESS_DENIED);
        }*/


        // 1. 신청 리스트 조회
        List<Applications> applications = applicationRepository.findByCompanionIdAndStatus(companionId, Applications.Status.PENDING);

        // 2. 신청한 유저 ID 목록 추출
        List<Long> userIds = applications.stream()
                .map(Applications::getUserId)
                .collect(Collectors.toList());

        // 3. UserRepository에서 유저 ID로 닉네임 조회
        Map<Long, String> userNicknames = userRepository.findNicknamesByUserIds(userIds)
                .stream()
                .collect(Collectors.toMap(
                        UserNicknameProjection::getUserId,
                        UserNicknameProjection::getNickname
                ));

        // 4. Mapper를 활용하여 Applications와 닉네임을 변환
        return applications.stream()
                .map(app -> LeaderApplicationMapper.INSTANCE.toDto(app, userNicknames.get(app.getUserId())))
                .collect(Collectors.toList());
    }

    /**
     * 리더의 신청 승인
     *
     * @param applicationId 승인하고 싶은 신청 ID
     //* @param userId        신청을 승인하려는 리더의 ID
     */
    @Transactional
    public void acceptApplication(Long applicationId) {


        // 신청 ID로 조회
        Applications application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(APPLICATION_NOT_FOUND));
/*

        // 리더인지 확인
        Long companionId = application.getCompanionId();
        Long company = companionRepository.findLeaderIdByCompanyId(companionId)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        if (!userId.equals(company)) {
            throw new CustomException(ACCESS_DENIED);
        }
*/
        // 상태 바꾸기
        if (!application.getStatus().equals(Applications.Status.PENDING)) {
            throw new CustomException(INVALID_APPLICATION_STATUS);
        }
        application.setStatus(Applications.Status.ACCEPTED);
        applicationRepository.save(application);

        // User 조회
        User user = userRepository.findById(application.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // CompanionMember 생성 및 설정
        //CompanionMemberId companionMemberId = new CompanionMemberId(companionId, user.getId());
        CompanionMemberId companionMemberId = new CompanionMemberId(application.getCompanionId(), user.getId());
        Companion_member companionMember = new Companion_member();
        companionMember.setId(companionMemberId);
        companionMember.setUser(user); // 연관 관계 설정
        companionMember.setJoinedDate(LocalDateTime.now());

        // 저장
        companionMemberRepository.save(companionMember);
    }

    /**
     * 리더의 신청 거절
     *
     * @param applicationId 거절하고 싶은 신청 ID
     //* @param userId        신청을 거절ㅌ`하려는 리더의 ID
     */
    public void rejectApplication(Long applicationId) {

        // 신청 ID로 조회
        Applications application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(APPLICATION_NOT_FOUND));

        /*
        // 리더인지 확인 -> jwt
        Long companyId = application.getCompanionId();
        Long company = companionRepository.findLeaderIdByCompanyId(companyId)
                .orElseThrow(() -> new CustomException(COMPANY_NOT_FOUND));

        if (!userId.equals(company)) {
            throw new CustomException(ACCESS_DENIED);
        }

         */

        // 상태 바꾸기
        if (!application.getStatus().equals(Applications.Status.PENDING)) {
            throw new CustomException(INVALID_APPLICATION_STATUS);
        }
        application.setStatus(Applications.Status.REJECTED);
        applicationRepository.save(application);
    }
}

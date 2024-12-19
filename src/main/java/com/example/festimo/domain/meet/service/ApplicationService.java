package com.example.festimo.domain.meet.service;

import org.springframework.stereotype.Service;

import com.example.festimo.domain.meet.dto.ApplicationResponse;
import com.example.festimo.domain.meet.entity.Applications;
import com.example.festimo.domain.meet.mapper.ApplicationMapper;
import com.example.festimo.domain.meet.repository.ApplicationRepository;
import com.example.festimo.domain.meet.repository.CompanyRepository;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.CustomException;


import static com.example.festimo.exception.ErrorCode.*;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public ApplicationService(ApplicationRepository applicationRepository, CompanyRepository companyRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    //신청 생성
    public ApplicationResponse createApplication(Long userId, Long companyId){

        //userId 확인
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new CustomException(USER_NOT_FOUND);
        }

        // companyId 존재 확인
        boolean companyExists = companyRepository.existsById(companyId);
        if (!companyExists) {
            throw new CustomException(COMPANY_NOT_FOUND);
        }

        // 이미 신청이 있는지 확인 (
        boolean applicationExists = applicationRepository.existsByUserIdAndCompanyId(userId, companyId);
        if (applicationExists) {
            throw new CustomException(DUPLICATE_APPLICATION);
        }


        Applications application = new Applications(userId,companyId);
        application = applicationRepository.save(application);

        return ApplicationMapper.INSTANCE.toDto(application);
    }
}

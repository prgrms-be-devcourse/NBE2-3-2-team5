package com.example.festimo.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.festimo.admin.Mapper.AdminMapper;
import com.example.festimo.admin.dto.AdminDTO;
import com.example.festimo.admin.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;

    @Autowired
    public AdminService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //모든 회원 조회
    public Page<AdminDTO> getAllUsers(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(AdminMapper.INSTANCE::toDto);
    }

}

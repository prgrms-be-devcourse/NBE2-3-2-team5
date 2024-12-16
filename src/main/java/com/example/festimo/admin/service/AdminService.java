package com.example.festimo.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
    public List<AdminDTO> getAllUsers(){

        return userRepository.findAll().stream()
                .map(AdminMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

}

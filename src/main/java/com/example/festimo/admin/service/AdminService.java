package com.example.festimo.admin.service;

import com.example.festimo.admin.domain.Users;
import com.example.festimo.admin.dto.AdminUpdateUserDTO;
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

    //회원 정보 수정
    public AdminDTO updateUser(Long userId, AdminUpdateUserDTO dto) {

        //유저가 존재하는지 확인
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        //업데이트
        user.setUserName(dto.getUserName());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setGender(Users.Gender.valueOf(dto.getGender()));
        user.setRatingAvg(dto.getRatingAvg());

        Users updatedUser = userRepository.save(user);

        return AdminMapper.INSTANCE.toDto(updatedUser);

    }

}

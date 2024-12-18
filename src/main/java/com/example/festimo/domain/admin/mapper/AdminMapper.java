package com.example.festimo.domain.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.festimo.domain.admin.entity.Users;
import com.example.festimo.domain.admin.dto.AdminDTO;

@Mapper
public interface AdminMapper {

    // Mapper 인스턴스 생성
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    // Entity → DTO 매핑
    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "ratingAvg", target = "ratingAvg")
    AdminDTO toDto(Users user);
}

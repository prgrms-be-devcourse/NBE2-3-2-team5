package com.example.festimo.domain.admin.mapper;

import com.example.festimo.domain.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.festimo.domain.admin.dto.AdminDTO;

@Mapper
public interface AdminMapper {

    // Mapper 인스턴스 생성
    AdminMapper INSTANCE = Mappers.getMapper(AdminMapper.class);

    // Entity → DTO 매핑
    @Mapping(source = "id", target = "userId") // User의 `id`를 AdminDTO의 `userId`로 매핑
    @Mapping(source = "userName", target = "userName")
    @Mapping(source = "nickname", target = "nickname")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "role", target = "role")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "ratingAvg", target = "ratingAvg")
    AdminDTO toDto(User user);
}

package com.example.festimo.domain.admin.mapper;

import com.example.festimo.domain.admin.entity.AdminReviews;
import com.example.festimo.domain.admin.dto.AdminReviewDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminReviewMapper {

    // Mapper 인스턴스 생성
    AdminReviewMapper INSTANCE = Mappers.getMapper(AdminReviewMapper.class);

    // 엔티티를 DTO로 변환
    @Mapping(source = "reviewId", target = "reviewId")
    @Mapping(source = "reviewerId", target = "reviewerId")
    @Mapping(source = "revieweeId", target = "revieweeId")
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    AdminReviewDTO toDTO(AdminReviews adminReviews);

}

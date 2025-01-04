package com.example.festimo.domain.admin.mapper;


import com.example.festimo.domain.review.domain.Review;
import com.example.festimo.domain.review.dto.ReviewResponseDTO;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminReviewMapper {
    AdminReviewMapper INSTANCE = Mappers.getMapper(AdminReviewMapper.class);


    // Review 엔티티 -> ReviewResponseDTO
    ReviewResponseDTO toResponseDTO(Review review);
}

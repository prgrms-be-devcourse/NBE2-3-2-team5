package com.example.festimo.domain.admin.service;

import com.example.festimo.domain.admin.mapper.AdminReviewMapper;
import com.example.festimo.domain.admin.dto.AdminReviewDTO;
import com.example.festimo.domain.admin.repository.AdminReviewsRepository;
import com.example.festimo.exception.CustomException;
import com.example.festimo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminReviewService {

    private final AdminReviewsRepository adminReviewsRepository;

    @Autowired
    public AdminReviewService(AdminReviewsRepository adminReviewsRepository) {
        this.adminReviewsRepository = adminReviewsRepository;
    }

    //리뷰 조회
    public Page<AdminReviewDTO> getReviews(int page, int size) {

        Pageable pageable= PageRequest.of(page,size);
        return adminReviewsRepository.findAll(pageable)
                .map(AdminReviewMapper.INSTANCE::toDTO);

    }

    //리뷰 삭제
    public void deleteReview(Long reviewId) {
        //존재하는지 확인
         adminReviewsRepository.findById(reviewId)
                        .orElseThrow(()-> new CustomException(ErrorCode. REVIEW_NOT_FOUND));

        adminReviewsRepository.deleteById(reviewId);
    }
}

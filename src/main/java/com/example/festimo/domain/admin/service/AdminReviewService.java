package com.example.festimo.domain.admin.service;

import com.example.festimo.domain.admin.mapper.AdminReviewMapper;

import com.example.festimo.domain.review.domain.Review;
import com.example.festimo.domain.review.dto.ReviewResponseDTO;
import com.example.festimo.domain.review.repository.ReviewRepository;
import com.example.festimo.exception.CustomException;
import com.example.festimo.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminReviewService {


    private final ReviewRepository reviewRepository;


    @Autowired
    public AdminReviewService( ReviewRepository reviewRepository) {

        this.reviewRepository = reviewRepository;
    }

    // 모든 리뷰 조회 (페이징)
    @Transactional
    public Page<ReviewResponseDTO> getAllReviews(Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        if (reviewPage.isEmpty()) {
            throw new CustomException(ErrorCode.REVIEW_NOT_FOUND);
        }
        return reviewPage.map(AdminReviewMapper.INSTANCE::toResponseDTO);
    }

    //리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId) {
        // 리뷰 존재 여부 확인
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰 삭제
        reviewRepository.delete(review);
    }

}

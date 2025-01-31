package com.example.festimo.domain.meet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicantReviewResponse {
    private int rating; // 평점
    private String content; // 리뷰 내용

    public ApplicantReviewResponse(int rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
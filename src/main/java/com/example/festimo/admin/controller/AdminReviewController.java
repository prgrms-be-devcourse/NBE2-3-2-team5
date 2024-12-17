package com.example.festimo.admin.controller;

import com.example.festimo.admin.dto.AdminReviewDTO;
import com.example.festimo.admin.service.AdminReviewService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/reviews")
public class AdminReviewController {

    AdminReviewService adminReviewService;

    @Autowired
    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    @GetMapping()
    @Operation(summary = "관리자의 리뷰 조회", description = "모든 리뷰 조회")
    public ResponseEntity<Page<AdminReviewDTO>> getReviews(
            @RequestParam(defaultValue="0") int page,
            @RequestParam (defaultValue = "10") int size){
        Page<AdminReviewDTO> reviews = adminReviewService.getReviews(page, size);
        return ResponseEntity.ok(reviews);

    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "관리자의 리뷰 삭제", description = "특정 리뷰 삭제")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        adminReviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}

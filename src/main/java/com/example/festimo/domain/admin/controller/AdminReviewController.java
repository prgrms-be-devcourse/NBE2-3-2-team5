package com.example.festimo.domain.admin.controller;

import com.example.festimo.domain.admin.dto.AdminReviewDTO;
import com.example.festimo.domain.admin.service.AdminReviewService;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/reviews")
@Tag(name = "관리자 API", description = "관리자가 리뷰를 관리하는 API")
public class AdminReviewController {

    AdminReviewService adminReviewService;

    @Autowired
    public AdminReviewController(AdminReviewService adminReviewService) {
        this.adminReviewService = adminReviewService;
    }

    /**
     * 관리자의 리뷰 조회
     * @param page 조회할 페이지 번호 (기본값: 0)
     * @param size 한 페이지에 표시할 리뷰 개수 (기본값: 10)
     * @return 페이지네이션된 리뷰 목록
     */
    @GetMapping()
    @Operation(summary = "관리자의 리뷰 조회", description = "모든 리뷰 조회")
    public ResponseEntity<Page<AdminReviewDTO>> getReviews(
            @RequestParam(defaultValue="0") int page,
            @RequestParam (defaultValue = "10") int size){
        Page<AdminReviewDTO> reviews = adminReviewService.getReviews(page, size);
        return ResponseEntity.ok(reviews);

    }

    /**
     * 관리자의 리뷰 삭제
     * @param reviewId 삭제할 리뷰의 ID
     * @return 성공적인 삭제 응답
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "관리자의 리뷰 삭제", description = "특정 리뷰 삭제")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        adminReviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}

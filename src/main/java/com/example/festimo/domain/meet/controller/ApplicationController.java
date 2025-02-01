package com.example.festimo.domain.meet.controller;

import java.util.List;

import com.example.festimo.domain.meet.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.festimo.domain.meet.service.ApplicationService;
import com.example.festimo.global.utils.jwt.JwtTokenProvider;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/meet")
@Tag(name = "동행 API", description = "동행 관련 API")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 동행 신청 API
     *
     * @param request 사용자의 userId와 companyId를 포함하는 ApplicationRequest 객체
     * @return 생성된 신청 정보를 반환, HTTP 상태 코드는 CREATED(201)
     */

    @PostMapping("/applications")
    @Operation(summary = "동행 신청")
    public ResponseEntity<ApplicationResponse> apply(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ApplicationRequest request) {


        ApplicationResponse response = applicationService.createApplication(authorizationHeader, request.getCompanionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리더의 동행 신청 리스트 조회 API
     *
     * @param companionId 조회할 동행의 ID
    //* @param userId    조회를 시도한 유저의 ID --> 로그인 하고 바꾸기!!!
     * @return 해당 동행을 신청한 유저의 정보
     */
    @GetMapping("/companion/{companionId}")
    @Operation(summary = "리더의 동행 신청 리스트")
    public ResponseEntity<List<LeaderApplicationResponse>> getAllApplications(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long companionId
    ) {

        String email = getEmailFromHeader(authorizationHeader);

        List<LeaderApplicationResponse> responses = applicationService.getAllApplications(companionId,email);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    /**
     * 리더의 동행 신청 승인 API
     *
     * @param applicationId 승인하고 싶은 신청 ID
    // * @param userId        조회를 시도한 유저의 ID --> 로그인 하고 바꾸기!!!
     */
    @PostMapping("/{applicationId}/accept")
    @Operation(summary = "리더의 동행 신청 승인")
    public ResponseEntity<Void> acceptApplication(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long applicationId
    ) {

        String email = getEmailFromHeader(authorizationHeader);

        applicationService.acceptApplication(applicationId, email);
        return ResponseEntity.ok().build();
    }

    /**
     * 리더의 동행 신청 거절 API
     *
     * @param applicationId 거절하고 싶은 신청 ID
    //  * @param userId        조회를 시도한 유저의 ID
     */
    @PatchMapping("/{applicationId}/reject")
    @Operation(summary = "리더의 동행 신청 거절")
    public ResponseEntity<Void> rejectApplication(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long applicationId) {

        String email = getEmailFromHeader(authorizationHeader);

        applicationService.rejectApplication(applicationId,email);
        return ResponseEntity.ok().build();
    }

    /**
     * 신청자 리뷰 확인
     *
     * @param applicationId 확인하고 싶은 신청자의 신청 ID
     * @param page
     */
    @GetMapping("/{applicationId}/reviews")
    public ResponseEntity<Page<ApplicantReviewResponse>> getApplicantReview(
            @PathVariable Long applicationId,
            int page){

        Pageable pageable = PageRequest.of(page,5, Sort.by("createdAt").descending());
        Page <ApplicantReviewResponse> reviews = applicationService.getApplicantReviews(applicationId,pageable);

        if(reviews.isEmpty()){
            return ResponseEntity.noContent().build();   //리뷰가 없을 경우
        }

        return ResponseEntity.ok(reviews);
    }



    //Header에서 email
    private String getEmailFromHeader(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtTokenProvider.getEmailFromToken(token);
    }
}
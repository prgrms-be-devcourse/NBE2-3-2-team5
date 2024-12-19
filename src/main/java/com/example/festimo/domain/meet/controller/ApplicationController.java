package com.example.festimo.domain.meet.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.festimo.domain.meet.dto.ApplicationRequest;
import com.example.festimo.domain.meet.dto.ApplicationResponse;
import com.example.festimo.domain.meet.service.ApplicationService;
import com.example.festimo.domain.meet.dto.LeaderApplicationResponse;

@RestController
@RequestMapping("/applications")
@Tag(name = "동행 API", description ="동행 관련 API")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * 동행 신청 API
     *
     * @param request - 사용자의 userId와 companyId를 포함하는 ApplicationRequest 객체
     * @return  생성된 신청 정보를 반환, HTTP 상태 코드는 CREATED(201)
     */
    @PostMapping
    @Operation(summary= "동행 신청")
    public ResponseEntity<ApplicationResponse> apply (
            @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(request.getUserId(), request.getCompanyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리더의 동행 신청 리스트 조회 API
     *
     * @param companyId - 조회할 동행의 ID
     * @return LeaderApplicationResponse - 해당 동행을 신청한 유저의 정보
     */

    @GetMapping("/company/{companyId}")
    @Operation(summary = "리더의 동행 신청 리스트")
    public ResponseEntity<List<LeaderApplicationResponse>> getAllApplications(
            @PathVariable Long companyId
    ) {
        List<LeaderApplicationResponse> responses = applicationService.getAllApplications(companyId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}

package com.example.festimo.domain.meet.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.festimo.domain.meet.dto.ApplicationRequest;
import com.example.festimo.domain.meet.dto.ApplicationResponse;
import com.example.festimo.domain.meet.service.ApplicationService;

@RestController
@RequestMapping("/applications")
@Tag(name = "동행 API", description ="동행 관련 API")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @Operation(summary= "동행 신청")
    public ResponseEntity<ApplicationResponse> apply (
            @RequestBody ApplicationRequest request) {
        ApplicationResponse response = applicationService.createApplication(request.getUserId(), request.getCompanyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

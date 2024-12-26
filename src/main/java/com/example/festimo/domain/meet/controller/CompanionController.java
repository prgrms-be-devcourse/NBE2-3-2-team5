package com.example.festimo.domain.meet.controller;

import com.example.festimo.domain.meet.service.CompanionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/meet")
@Tag(name = "동행 API", description = "동행 관련 API")
public class CompanionController {

    CompanionService companionService;

    public CompanionController(CompanionService companionService) {
        this.companionService = companionService;
    }

    @DeleteMapping("/{companyId}/users/{userId}")
    @Operation(summary = "동행 취소")
    public ResponseEntity<Void>  deleteCompaion(
            @PathVariable Long companyId,
            @PathVariable Long userId
    ){
        companionService.deleteCompaion(companyId, userId);
        return ResponseEntity.noContent().build();
    }
}

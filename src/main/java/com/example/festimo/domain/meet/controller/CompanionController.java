package com.example.festimo.domain.meet.controller;

import com.example.festimo.domain.meet.dto.CompanionRequest;
import com.example.festimo.domain.meet.dto.CompanionResponse;
import com.example.festimo.domain.meet.service.CompanionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/meet")
@Tag(name = "동행 API", description = "동행 관련 API")
public class CompanionController {

    CompanionService companionService;

    public CompanionController(CompanionService companionService) {
        this.companionService = companionService;
    }

    /**
     * 동행 게시글 생성하면 동행 생성
     *
     */
    @PostMapping("/companions")
    @Operation(summary = "동행 생성")
    public ResponseEntity<Void> createCompanion(
            @RequestBody CompanionRequest request ) {

       companionService.createCompanion(request.getPostId(), request.getUserId());
       return ResponseEntity.status(HttpStatus.CREATED).build();

    }


    /**
     * 동행원의 동행 취소
     *
     * @param companionId 취소할 동행의 ID
    //* @param userId    취소할 유저의 ID --> 로그인 하고 바꾸기!!!
     */
    @DeleteMapping("/{companionId}/users/{userId}")

    @Operation(summary = "동행 취소")
    public ResponseEntity<Void>  deleteCompaion(
            @PathVariable Long companyId,
            @PathVariable Long userId
    ){
        companionService.deleteCompaion(companyId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 내 동행 찾기
     *
    * @param userId   조회할 유저의 ID --> 로그인 하고 바꾸기!!!
     */
    @GetMapping("/companions/mine/{userId}")
    @Operation(summary = "내 동행 찾기")
    public ResponseEntity<Map<String,Object>> getMyCompanions(
            @PathVariable Long userId
    ){

        //리더로 참여한 동행
        List<CompanionResponse> asLeader = companionService.getCompanionAsLeader(userId);

        //동행원으로 참여한 동행
        List<CompanionResponse> asMember = companionService.getCompanionAsMember(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("asLeader", asLeader);
        response.put("asMember", asMember);

        return ResponseEntity.ok(response);
    }
}

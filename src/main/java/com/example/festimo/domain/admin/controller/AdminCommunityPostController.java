package com.example.festimo.domain.admin.controller;

import com.example.festimo.domain.admin.service.AdminCommunityPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/companions")
@Tag(name = "게시글 관리 API", description = "관리자가 리뷰를 관리하는 API")
public class AdminCommunityPostController {

    private final AdminCommunityPostService adminCommunityPostService;

    @Autowired
    public AdminCommunityPostController(AdminCommunityPostService adminCommunityPostService) {
        this.adminCommunityPostService = adminCommunityPostService;
    }

    /**
     * 특정 게시글 삭제
     * @param companionId 삭제할 게시글의 ID
     */
    @DeleteMapping("/{companionId}")
    @Operation(summary = "관리자의 게시글 삭제", description = "특정 게시글을 삭제")
    public ResponseEntity<Void> deletePost(@PathVariable Long companionId) {
        adminCommunityPostService.deletePost(companionId);
        return ResponseEntity.noContent().build();
    }
}

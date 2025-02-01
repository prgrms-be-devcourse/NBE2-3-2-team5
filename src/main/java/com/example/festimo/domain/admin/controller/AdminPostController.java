package com.example.festimo.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.example.festimo.domain.admin.service.AdminPostService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/posts")
@Tag(name = "관리자 API", description = "관리자가 게시글을 관리하는 API")
public class AdminPostController {

    private final AdminPostService adminPostService;


    /**
     * 관리자의 게시글 삭제
     * @param postId 삭제할 게시글 ID
     */
    @DeleteMapping("/{postId}")
    @Operation(summary = "관리자의 게시글 삭제", description = "특정 게시글 삭제")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {

        adminPostService.deletePostById(postId);
        return ResponseEntity.noContent().build();

    }
}

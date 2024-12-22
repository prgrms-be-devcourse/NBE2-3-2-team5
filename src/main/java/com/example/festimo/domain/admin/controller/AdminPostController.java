package com.example.festimo.domain.admin.controller;

import com.example.festimo.domain.admin.service.AdminPostService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin/posts")
@Tag(name = "관리자 API", description = "관리자가 게시글을 관리하는 API")
public class AdminPostController {

    private final AdminPostService adminPostService;

    public AdminPostController(AdminPostService adminPostService) {
        this.adminPostService = adminPostService;
    }

    /**
     * 관리자의 게시글 삭제
     * @param postId 삭제할 게시글 ID
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {

        adminPostService.deletePostById(postId);
        return ResponseEntity.noContent().build();
    }
}

package com.example.festimo.domain.post.controller;

import com.example.festimo.domain.post.dto.*;
import com.example.festimo.domain.post.service.PostService;
import com.example.festimo.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companions")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 등록")
    @PostMapping
    public ResponseEntity<PostListResponse> createPost(@Valid @RequestBody PostRequest request, Authentication authentication) {
        PostListResponse responseDto = postService.createPost(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "게시글 전체 조회")
    @GetMapping
    public ResponseEntity<PageResponse<PostListResponse>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<PostListResponse> allPosts = postService.getAllPosts(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(allPosts);
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostById(@PathVariable Long postId, Authentication authentication) {
        PostDetailResponse postById = postService.getPostById(postId, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(postById);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        PostDetailResponse updatePost = postService.updatePost(postId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatePost);
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestBody DeletePostRequest request,
            Authentication authentication) {
        postService.deletePost(postId, request.getPassword(), authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "댓글 등록")
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest request,
            Authentication authentication) {
        CommentResponse comment = postService.createComment(postId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{postId}/comments/{sequence}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Integer sequence,
            @RequestBody @Valid UpdateCommentRequest request,
            Authentication authentication
    ) {
        CommentResponse comment = postService.updateComment(postId, sequence, request, authentication);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{postId}/comments/{sequence}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Integer sequence,
            Authentication authentication
    ) {
        postService.deleteComment(postId, sequence, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
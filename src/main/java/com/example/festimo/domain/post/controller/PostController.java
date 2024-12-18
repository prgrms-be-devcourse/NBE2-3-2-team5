package com.example.festimo.domain.post.controller;

import com.example.festimo.domain.post.dto.*;
import com.example.festimo.domain.post.service.PostService;
import com.example.festimo.global.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companions")
public class PostController {

    private final PostService postService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<PostListResponse> createPost(@Valid @RequestBody PostRequest request) {
        PostListResponse responseDto = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 게시글 전체 조회
    @GetMapping
    public ResponseEntity<PageResponse<PostListResponse>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<PostListResponse> allPosts = postService.getAllPosts(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(allPosts);
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostById(@PathVariable Long postId) {
        PostDetailResponse postById = postService.getPostById(postId);
        return ResponseEntity.status(HttpStatus.OK).body(postById);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        PostDetailResponse updatePost = postService.updatePost(postId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatePost);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestBody DeletePostRequest request) {
        postService.deletePost(postId, request.getPassword());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 댓글 등록
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest request) {
        CommentResponse comment = postService.createComment(postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    // 댓글 수정
    @PutMapping("/{postId}/comments/{sequence}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long postId,
            @PathVariable Integer sequence,
            @RequestBody @Valid UpdateCommentRequest request
    ) {
        CommentResponse comment = postService.updateComment(postId, sequence, request);
        return ResponseEntity.status(HttpStatus.OK).body(comment);
    }
}
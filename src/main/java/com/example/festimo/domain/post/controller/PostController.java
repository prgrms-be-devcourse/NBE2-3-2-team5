package com.example.festimo.domain.post.controller;

import com.example.festimo.domain.post.dto.PostRequest;
import com.example.festimo.domain.post.dto.PostResponse;
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

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
        PostResponse responseDto = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostResponse>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        PageResponse<PostResponse> allPosts = postService.getAllPosts(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(allPosts);
    }
}
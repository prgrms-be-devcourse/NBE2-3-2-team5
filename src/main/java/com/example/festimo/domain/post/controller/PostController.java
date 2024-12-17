package com.example.festimo.domain.post.controller;

import com.example.festimo.domain.post.dto.PostRequestDto;
import com.example.festimo.domain.post.dto.PostResponseDto;
import com.example.festimo.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/companions")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(@Valid @RequestBody PostRequestDto request) {
        PostResponseDto responseDto = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}

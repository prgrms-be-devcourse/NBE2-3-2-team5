package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.PostRequestDto;
import com.example.festimo.domain.post.dto.PostResponseDto;
import jakarta.validation.Valid;

public interface PostService {
    PostResponseDto createPost(@Valid PostRequestDto postDto);
}

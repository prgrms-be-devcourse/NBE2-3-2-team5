package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.PostRequest;
import com.example.festimo.domain.post.dto.PostResponse;
import com.example.festimo.global.dto.PageResponse;
import jakarta.validation.Valid;

public interface PostService {
    PostResponse createPost(@Valid PostRequest postDto);
    PageResponse<PostResponse> getAllPosts(int page, int size);
}
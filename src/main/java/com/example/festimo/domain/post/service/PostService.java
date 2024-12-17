package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.PostDetailResponse;
import com.example.festimo.domain.post.dto.PostRequest;
import com.example.festimo.domain.post.dto.PostListResponse;
import com.example.festimo.global.dto.PageResponse;
import jakarta.validation.Valid;

public interface PostService {
    PostListResponse createPost(@Valid PostRequest postDto);
    PageResponse<PostListResponse> getAllPosts(int page, int size);
    PostDetailResponse getPostById(Long postId);
}
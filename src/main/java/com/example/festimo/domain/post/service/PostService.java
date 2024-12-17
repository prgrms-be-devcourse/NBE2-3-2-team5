package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.PostRequest;
import com.example.festimo.domain.post.dto.PostResponse;
import com.example.festimo.global.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;

public interface PostService {
    PostResponse createPost(@Valid PostRequest postDto);
    PageResponse<PostResponse> getAllPosts(Pageable pageable);
}

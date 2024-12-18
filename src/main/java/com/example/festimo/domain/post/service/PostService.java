package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.*;
import com.example.festimo.global.dto.PageResponse;
import jakarta.validation.Valid;

public interface PostService {
    PostListResponse createPost(@Valid PostRequest postDto);
    PageResponse<PostListResponse> getAllPosts(int page, int size);
    PostDetailResponse getPostById(Long postId);
    PostDetailResponse updatePost(Long postId, @Valid UpdatePostRequest request);
    void deletePost(Long postId, String password);
    CommentResponse createComment(Long postId, @Valid CommentRequest commentDto);
    CommentResponse updateComment(Long postId, Integer sequence, @Valid UpdateCommentRequest commentDto);
}
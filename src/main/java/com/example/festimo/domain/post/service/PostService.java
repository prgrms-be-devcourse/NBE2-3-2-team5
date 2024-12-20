package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.*;
import com.example.festimo.global.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

public interface PostService {
    PostListResponse createPost(@Valid PostRequest postDto, Authentication authentication);
    PageResponse<PostListResponse> getAllPosts(int page, int size);
    PostDetailResponse getPostById(Long postId, Authentication authentication);
    PostDetailResponse updatePost(Long postId, @Valid UpdatePostRequest request);
    void deletePost(Long postId, String password, Authentication authentication);
    CommentResponse createComment(Long postId, @Valid CommentRequest commentDto, Authentication authentication);
    CommentResponse updateComment(Long postId, Integer sequence, @Valid UpdateCommentRequest commentDto, Authentication authentication);
    void deleteComment(Long postId, Integer sequence, Authentication authentication);
}
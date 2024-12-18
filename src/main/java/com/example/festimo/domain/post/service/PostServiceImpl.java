package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.*;
import com.example.festimo.domain.post.entity.Comment;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.entity.PostCategory;
import com.example.festimo.domain.post.repository.CommentRepository;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.exception.InvalidPageRequest;
import com.example.festimo.exception.InvalidPasswordException;
import com.example.festimo.exception.NoContent;
import com.example.festimo.exception.PostNotFound;
import com.example.festimo.global.dto.PageResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;

    // 게시글 등록
    @Transactional
    @Override
    public PostListResponse createPost(@Valid PostRequest request) {
        Post post = modelMapper.map(request, Post.class);
        Post savedEntity = postRepository.save(post);
        return modelMapper.map(savedEntity, PostListResponse.class);
    }

    // 게시글 전체 조회
    @Override
    public PageResponse<PostListResponse> getAllPosts(int page, int size) {
        if (page < 1 || size <= 0) {
            throw new InvalidPageRequest();
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> posts = postRepository.findAll(pageable);

        if (posts.isEmpty()) {
            throw new NoContent();
        }

        Page<PostListResponse> responsePage = posts.map(post -> modelMapper.map(post, PostListResponse.class));
        return new PageResponse<>(responsePage);
    }

    // 게시글 상세 조회
    @Transactional
    @Override
    public PostDetailResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFound());
        post.increaseViews();

        List<CommentResponse> comments = post.getComments().stream()
                .map(comment -> new CommentResponse(
                        comment.getSequence(),
                        comment.getComment(),
                        comment.getNickname(),
                        post.getId()))
                .toList();

        PostDetailResponse response = modelMapper.map(post, PostDetailResponse.class);
        response.setComments(comments);
        return response;
    }

    // 게시글 수정
    @Transactional
    @Override
    public PostDetailResponse updatePost(Long postId, @Valid UpdatePostRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFound());

        if (ObjectUtils.isEmpty(request.getPassword()) ||
                !post.getPassword().equals(request.getPassword())) {
            throw new InvalidPasswordException();
        }

        String newTitle = request.getTitle() != null ? request.getTitle() : post.getTitle();
        String newContent = request.getContent() != null ? request.getContent() : post.getContent();
        PostCategory newCategory = request.getCategory() != null ? request.getCategory() : post.getCategory();

        post.update(newTitle, newContent, newCategory);
        return modelMapper.map(post, PostDetailResponse.class);
    }

    // 게시글 삭제
    @Transactional
    @Override
    public void deletePost(Long postId, String password) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFound());

        if (!post.getPassword().equals(password)) {
            throw new InvalidPasswordException();
        }

        postRepository.delete(post);
    }

    // 댓글 등록
    @Transactional
    @Override
    public CommentResponse createComment(Long postId, @Valid CommentRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFound());

        Integer maxSequence = commentRepository.findMaxSequenceByPost(post);
        Integer nextSequence = maxSequence + 1;

        Comment comment = Comment.builder()
                .comment(request.getComment())
                .nickname(request.getNickname())
                .post(post)
                .sequence(nextSequence)
                .build();
        commentRepository.save(comment);

        return modelMapper.map(comment, CommentResponse.class);
    }
}
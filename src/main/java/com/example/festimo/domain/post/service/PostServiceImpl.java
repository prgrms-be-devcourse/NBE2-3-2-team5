package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.*;
import com.example.festimo.domain.post.entity.Comment;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.entity.PostCategory;
import com.example.festimo.domain.post.repository.CommentRepository;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.domain.user.domain.User;
import com.example.festimo.domain.user.repository.UserRepository;
import com.example.festimo.exception.*;
import com.example.festimo.global.dto.PageResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;

    // 게시글 등록
    @Transactional
    @Override
    public PostListResponse createPost(@Valid PostRequest request, Authentication authentication) {
        validateAuthentication(authentication);

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);

        Post post = modelMapper.map(request, Post.class);
        post.setUser(user);
        post.setWriter(user.getNickname());
        post.setMail(user.getEmail());

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

    @Transactional
    @Override
    public PostDetailResponse getPostById(Long postId, Authentication authentication) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        post.increaseViews();

        boolean isOwner = false;
        boolean isAdmin = false;

        if (authentication != null && authentication.isAuthenticated()) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null) {
                isOwner = post.getUser().getId().equals(user.getId());
                isAdmin = user.getRole().equals(User.Role.ADMIN);
            }
        }

        List<CommentResponse> comments = post.getComments().stream()
                .map(comment -> new CommentResponse(
                        comment.getSequence(),
                        comment.getComment(),
                        comment.getNickname(),
                        post.getId(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()))
                .toList();

        PostDetailResponse response = modelMapper.map(post, PostDetailResponse.class);
        response.setOwner(isOwner);
        response.setAdmin(isAdmin);
        response.setComments(comments);
        return response;
    }

    // 게시글 수정
    @Transactional
    @Override
    public PostDetailResponse updatePost(Long postId, @Valid UpdatePostRequest request) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        if (ObjectUtils.isEmpty(request.getPassword()) || !post.getPassword().equals(request.getPassword())) {
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
    public void deletePost(Long postId, String password, Authentication authentication) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);

        if (post.getUser().equals(user)) {
            if (!post.getPassword().equals(password)) {
                throw new InvalidPasswordException();
            }
        } else if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new PostDeleteAuthorizationException();
        }

        postRepository.delete(post);
    }

    // 댓글 등록
    @Transactional
    @Override
    public CommentResponse createComment(Long postId, @Valid CommentRequest request, Authentication authentication) {
        validateAuthentication(authentication);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);

        Integer maxSequence = commentRepository.findMaxSequenceByPost(post);
        Integer nextSequence = (maxSequence == null) ? 1 : maxSequence + 1;

        Comment comment = Comment.builder()
                .comment(request.getComment())
                .nickname(user.getNickname())
                .post(post)
                .sequence(nextSequence)
                .build();
        commentRepository.save(comment);

        return modelMapper.map(comment, CommentResponse.class);
    }

    // 댓글 수정
    @Transactional
    @Override
    public CommentResponse updateComment(Long postId, Integer sequence, @Valid UpdateCommentRequest request, Authentication authentication) {
        validateAuthentication(authentication);

        Comment comment = commentRepository.findByPostIdAndSequence(postId, sequence).orElseThrow(CommentNotFound::new);
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);

        if (!comment.getNickname().equals(user.getNickname())) {
            throw new CommentUpdateAuthorizationException();
        }

        comment.updateContent(request.getComment());
        commentRepository.saveAndFlush(comment);

        return modelMapper.map(comment, CommentResponse.class);
    }

    private static void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }
    }

    // 댓글 삭제
    @Transactional
    @Override
    public void deleteComment(Long postId, Integer sequence, Authentication authentication) {
        Comment comment = commentRepository.findByPostIdAndSequence(postId, sequence).orElseThrow(CommentNotFound::new);
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);

        if (!comment.getNickname().equals(user.getNickname()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new CommentDeleteAuthorizationException();
        }

        commentRepository.delete(comment);
    }
}
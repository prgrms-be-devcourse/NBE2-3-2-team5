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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@EnableCaching
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;

    // 게시글 등록
    @Transactional
    @Override
    public void createPost(
            @Valid PostRequest request,
            Authentication authentication) {
        User user = validateAuthenticationAndGetUser(authentication);

        Post post = Post.builder()
                .title(request.getTitle())
                .writer(user.getNickname())
                .mail(user.getEmail())
                .password(request.getPassword())
                .content(request.getContent())
                .category(request.getCategory())
                .tags(request.getTags() != null ? new HashSet<>(request.getTags()) : new HashSet<>())
                .user(user)
                .build();

        postRepository.save(post);
        clearWeeklyTopPostsCache();
    }

    // 게시글 전체 조회
    @Override
    public PageResponse<PostListResponse> getAllPosts(int page, int size) {
        if (page < 1 || size <= 0) {
            throw new InvalidPageRequest();
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Post> posts = postRepository.findAll(pageable);

        if (posts.isEmpty()) {
            throw new NoContent();
        }

        List<PostListResponse> responseList = posts.stream()
                .map(PostListResponse::new)
                .collect(Collectors.toList());

        return new PageResponse<>(new PageImpl<>(responseList, pageable, posts.getTotalElements()));
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    @Override
    public PostDetailResponse getPostById(Long postId, boolean incrementView, Authentication authentication) {
        User user = validateAuthenticationAndGetUser(authentication);

        Post post = postRepository.findByIdWithDetails(postId).orElseThrow(PostNotFound::new);

        if (incrementView) {
            postRepository.incrementViews(postId);
        }

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole().equals(User.Role.ADMIN);

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
        response.setTags(post.getTags());
        response.setReplies(comments.size());
        return response;
    }

    // 게시글 수정
    @Transactional
    @Override
    public PostDetailResponse updatePost(Long postId, @Valid UpdatePostRequest request) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(UnauthorizedException::new);

        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        if (ObjectUtils.isEmpty(request.getPassword()) || !post.getPassword().equals(request.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (request.getTitle() == null && request.getContent() == null && request.getCategory() == null) {
            throw new IllegalArgumentException("수정할 필드가 없습니다.");
        }

        String newTitle = request.getTitle() != null ? request.getTitle() : post.getTitle();
        String newContent = request.getContent() != null ? request.getContent() : post.getContent();
        PostCategory newCategory = request.getCategory() != null ? request.getCategory() : post.getCategory();

        post.update(newTitle, newContent, newCategory);
        postRepository.saveAndFlush(post);

        PostDetailResponse response = modelMapper.map(post, PostDetailResponse.class);
        response.setOwner(post.getUser().getId().equals(user.getId()));
        response.setAdmin(user.getRole().equals(User.Role.ADMIN));

        clearWeeklyTopPostsCache();
        return response;
    }

    // 게시글 삭제
    @Transactional
    @Override
    public void deletePost(Long postId, String password, Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UnauthorizedException::new);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        if (post.getUser().equals(user)) {
            if (!post.getPassword().equals(password)) {
                throw new InvalidPasswordException();
            }
        } else if (!user.getRole().equals(User.Role.ADMIN)) {
            throw new PostDeleteAuthorizationException();
        }

        postRepository.delete(post);
        clearWeeklyTopPostsCache();
    }

    // 주간 인기 게시글
    @Cacheable(value = "posts:weeklyTopPosts",
            key = "#root.method.name + '_' + T(java.time.LocalDate).now().toString()",
            unless = "#result == null || #result.isEmpty()")
    @Transactional(readOnly = true)
    public List<PostListResponse> getCachedWeeklyTopPosts() {
        try {
            LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
            List<Post> posts = postRepository.findTopPostsOfWeek(lastWeek);

            return posts.stream()
                    .map(post -> {
                        Hibernate.initialize(post.getTags());
                        return new PostListResponse(post);
                    })
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("주간 인기 게시물 조회 중 오류 발생", e);
            return Collections.emptyList();
        }
    }

    @CacheEvict(value = "posts:weeklyTopPosts")
    public void clearWeeklyTopPostsCache() {
        // 캐시 초기화
    }

    // 게시글 검색
    @Override
    public List<PostListResponse> searchPosts(String keyword) {
        return postRepository.searchPostsByKeyword(keyword)
                .stream()
                .map(PostListResponse::new)
                .collect(Collectors.toList());
    }

    // 댓글 등록
    @Transactional
    @Override
    public CommentResponse createComment(Long postId, @Valid CommentRequest request, Authentication authentication) {
        User user = validateAuthenticationAndGetUser(authentication);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

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
        User user = validateAuthenticationAndGetUser(authentication);

        if (!postRepository.existsById(postId)) {
            throw new PostNotFound();
        }

        Comment comment = commentRepository.findByPostIdAndSequence(postId, sequence).orElseThrow(CommentNotFound::new);

        if (!comment.getNickname().equals(user.getNickname())) {
            throw new CommentUpdateAuthorizationException();
        }

        comment.updateContent(request.getComment());
        commentRepository.saveAndFlush(comment);

        return modelMapper.map(comment, CommentResponse.class);
    }

    private User validateAuthenticationAndGetUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException();
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(UnauthorizedException::new);
    }

    // 댓글 삭제
    @Transactional
    @Override
    public void deleteComment(Long postId, Integer sequence, Authentication authentication) {
        User user = validateAuthenticationAndGetUser(authentication);

        if (!postRepository.existsById(postId)) {
            throw new PostNotFound();
        }

        Comment comment = commentRepository.findByPostIdAndSequence(postId, sequence).orElseThrow(CommentNotFound::new);

        if (!comment.getNickname().equals(user.getNickname()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new CommentDeleteAuthorizationException();
        }

        commentRepository.delete(comment);
    }

    // 좋아요
    @Transactional
    @Override
    public void toggleLike(Long postId, Authentication authentication) {
        User user = validateAuthenticationAndGetUser(authentication);
        Post post = postRepository.findById(postId).orElseThrow(PostNotFound::new);

        post.toggleLike(user);
        postRepository.save(post);
    }
}
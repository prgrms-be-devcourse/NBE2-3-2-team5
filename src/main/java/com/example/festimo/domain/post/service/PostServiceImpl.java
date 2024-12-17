package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.PostRequest;
import com.example.festimo.domain.post.dto.PostResponse;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import com.example.festimo.global.dto.PageResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public PostResponse createPost(@Valid PostRequest postDto) {
        Post post = modelMapper.map(postDto, Post.class);
        Post savedEntity = postRepository.save(post);
        return modelMapper.map(savedEntity, PostResponse.class);
    }

    public PageResponse<PostResponse> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostResponse> responsePage = posts.map(post -> modelMapper.map(post, PostResponse.class));

        return new PageResponse<>(responsePage);
    }
}
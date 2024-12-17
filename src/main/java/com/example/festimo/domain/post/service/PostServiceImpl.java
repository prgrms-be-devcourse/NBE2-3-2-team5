package com.example.festimo.domain.post.service;

import com.example.festimo.domain.post.dto.PostRequestDto;
import com.example.festimo.domain.post.dto.PostResponseDto;
import com.example.festimo.domain.post.entity.Post;
import com.example.festimo.domain.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public PostResponseDto createPost(@Valid PostRequestDto postDto) {
        Post post = modelMapper.map(postDto, Post.class);
        Post savedEntity = postRepository.save(post);
        return modelMapper.map(savedEntity, PostResponseDto.class);
    }
}
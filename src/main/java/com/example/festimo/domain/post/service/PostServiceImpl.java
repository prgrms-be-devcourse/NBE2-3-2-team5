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

        if (postDto.getTitle().length() > 50) {
            throw new IllegalArgumentException("제목은 50자 이하로 입력해주세요.");
        }
        if (postDto.getPassword().length() < 4) {
            throw new IllegalArgumentException("비밀번호는 최소 4자 이상이어야 합니다.");
        }

        Post post = modelMapper.map(postDto, Post.class);
        Post savedEntity = postRepository.save(post);
        return modelMapper.map(savedEntity, PostResponseDto.class);
    }
}
